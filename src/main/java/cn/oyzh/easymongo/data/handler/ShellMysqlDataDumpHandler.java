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
public class ShellMysqlDataDumpHandler extends DBDataDumpHandler {

    public ShellMysqlDataDumpHandler(MongoClient dbClient, String dbName) {
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
            this.dumpTable();
        } else if (this.dumpType == 2) {
            MongoCollection table = new MongoCollection();
            table.setDbName(this.dbName);
            table.setName(this.tableName);
            this.dumpTable(table);
            this.writeTail();
            this.fileWriter.close();
            this.message("Dump Finished");
            this.message("Dump File To -> " + this.dumpFile.getPath());
        }
    }

    protected void dumpTable() throws InterruptedException, IOException {
        List<MongoCollection> tables = this.dbClient.selectCollections(this.dbName);
        if (CollectionUtil.isNotEmpty(tables)) {
            for (MongoCollection table : tables) {
                this.checkInterrupt();
                this.dumpTable(table);
            }
            this.processed(tables.size());
        }
    }

    protected void dumpTable(MongoCollection table) throws InterruptedException, IOException {
        String line0 = "";
        String line1 = "// ----------------------------";
        String line2 = "// Collection structure for " + table.getName();
        String line3 = "// ----------------------------";
        String line4 = "db.getCollection(\"" + table.getName() + "\").drop();";
        String line5 = "db.createCollection(\"" + table.getName() + "\");";
        this.message("Dumping Collection " + table.getName());
        this.fileWriter.appendLines(List.of(line0, line1, line2, line3, line4, line5));
        if (this.isDumpRecord()) {
            this.message("Dumping Records of Collection " + table.getName());
            this.dumpRecord(table.getName());
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

