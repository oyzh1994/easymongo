package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoSelectRecordParam;
import cn.oyzh.easymongo.util.MongoDataUtil;

import java.io.IOException;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/10
 */
public class ShellMongoDataDumpHandler extends DBDataDumpHandler {

    public ShellMongoDataDumpHandler(MongoClient dbClient, String dbName) {
        super(dbClient, dbName);
    }

    @Override
    public void doDump() throws Exception {
        if (this.fileWriter == null || this.dumpType == null || this.dataType == null) {
            throw new RuntimeException("parameter invalid!");
        }
        this.message("Dump Starting");
        this.writeHeader();
        if (this.dumpType == 1) {
            this.dumpCollection();
        } else if (this.dumpType == 2) {
            MongoCollection collection = new MongoCollection();
            collection.setDbName(this.dbName);
            collection.setName(this.tableName);
            this.dumpCollection(collection);
            this.writeTail();
            this.fileWriter.close();
            this.message("Dump Finished");
            this.message("Dump File To -> " + this.dumpFile.getPath());
        }
    }

    protected void dumpCollection() throws InterruptedException, IOException {
        List<MongoCollection> collections = this.dbClient.listCollections(this.dbName);
        if (CollectionUtil.isNotEmpty(collections)) {
            for (MongoCollection table : collections) {
                this.checkInterrupt();
                this.dumpCollection(table);
            }
            this.processed(collections.size());
        }
    }

    protected void dumpCollection(MongoCollection collection) throws InterruptedException, IOException {
        String line0 = "";
        String line1 = "// ----------------------------";
        String line2 = "// Collection structure for " + collection.getName();
        String line3 = "// ----------------------------";
        String line4 = "db.getCollection(\"" + collection.getName() + "\").drop();";
        String line5 = "db.createCollection(\"" + collection.getName() + "\");";
        this.message("Dumping Collection " + collection.getName());
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3, line4, line5));
        if (this.isDumpRecord()) {
            this.message("Dumping Records of Collection " + collection.getName());
            this.dumpRecord(collection.getName());
        }
    }

    protected void dumpRecord(String tableName) throws InterruptedException, IOException {
        long start = 0;
        String line0 = "";
        String line1 = "// ----------------------------";
        String line2 = "// Documents of " + tableName;
        String line3 = "// ----------------------------";
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3));
        while (true) {
            this.checkInterrupt();
            long start1 = System.currentTimeMillis();
            MongoSelectRecordParam param = new MongoSelectRecordParam();
            param.setStart(start);
            param.setReadonly(true);
            param.setDbName(this.dbName);
            param.setCollectionName(tableName);
            param.setLimit((long) this.queryLimit);
            List<MongoRecord> records = this.dbClient.selectCollectionRecords(param);
            if (CollectionUtil.isEmpty(records)) {
                break;
            }
            long end1 = System.currentTimeMillis();
            JulLog.info("查询耗时: {}ms", (end1 - start1));
            long start2 = System.currentTimeMillis();
            List<String> inserts = MongoDataUtil.toInsertScript(records);
            this.fileWriter.appendLines(inserts);
            long end2 = System.currentTimeMillis();
            JulLog.info("写入耗时: {}ms", (end2 - start2));
            start += this.queryLimit;
            this.processed(records.size());
        }
    }

}

