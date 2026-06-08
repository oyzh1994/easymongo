package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.ShellMysqlDataTransportTable;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoSelectRecordParam;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/06
 */
public class ShellMysqlDataTransportHandler extends DBDataTransportHandler {

    @Override
    public void doTransport() throws Exception {
        this.message("Transport Starting");
        try {
            if (CollectionUtil.isNotEmpty(this.tables)) {
                for (ShellMysqlDataTransportTable table : this.tables) {
                    this.transportTable(table.getName());
                }
            }
        } catch (Exception ex) {
            this.exception(ex);
        } finally {
            this.message("Transport Finished");
        }
    }

    /**
     * 传输表
     *
     * @param tableName 表名称
     * @throws InterruptedException 异常
     */
    private void transportTable(String tableName) throws InterruptedException {
        this.checkInterrupt();
        // 删除表
        this.targetClient.dropCollection(this.targetDatabase, tableName);
        this.message("Drop Table " + tableName);
        this.processedIncr();

        // 创建表
        this.targetClient.clearCollection(this.targetDatabase, tableName);
        this.message("Create Table " + tableName);
        this.processedIncr();

        // 传输表
        this.message("Transport Table " + tableName + " Starting");
        long start = 0;
        while (true) {
            this.checkInterrupt();
            MongoSelectRecordParam param = new MongoSelectRecordParam();
            param.setStart(start);
            param.setReadonly(true);
            param.setCollectionName(tableName);
            param.setDbName(this.sourceDatabase);
            param.setLimit((long) this.selectLimit);
            List<MongoRecord> records = this.sourceClient.selectCollectionRecords(param);
            if (CollectionUtil.isEmpty(records)) {
                break;
            }
            this.addInsertSql(records);
            start += this.selectLimit;
        }
        this.message("Transport Table " + tableName + " Finished");
    }
}

