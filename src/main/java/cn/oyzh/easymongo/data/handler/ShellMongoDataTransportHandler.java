package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.dto.ShellMongoDataTransportCollection;
import cn.oyzh.easymongo.data.dto.ShellMongoDataTransportFunction;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoSelectRecordParam;
import org.bson.BsonValue;

import java.util.List;

/**
 * MongoDB 数据传输处理器
 *
 * @author oyzh
 * @since 2024/09/06
 */
public class ShellMongoDataTransportHandler extends DBDataTransportHandler {

    /**
     * 来源客户端
     */
    protected MongoClient sourceClient;

    /**
     * 目标客户端
     */
    protected MongoClient targetClient;

    /**
     * 表
     */
    protected List<ShellMongoDataTransportCollection> tables;

    /**
     * 函数
     */
    protected List<ShellMongoDataTransportFunction> functions;

    @Override
    public void doTransport() throws Exception {
        this.message("Transport Starting");
        try {
            if (CollectionUtil.isNotEmpty(this.tables)) {
                for (ShellMongoDataTransportCollection table : this.tables) {
                    this.transportTable(table.getName());
                }
            }
            if (CollectionUtil.isNotEmpty(this.functions)) {
                for (ShellMongoDataTransportFunction function : this.functions) {
                    this.transportFunction(function.getName());
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
        this.message("Drop Collection " + tableName);
        this.processedIncr();

        // 创建表
        this.targetClient.clearCollection(this.targetDatabase, tableName);
        this.message("Create Collection " + tableName);
        this.processedIncr();

        // 传输表
        this.message("Transport Collection " + tableName + " Starting");
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
        this.message("Transport Collection " + tableName + " Finished");
    }

    /**
     * 传输函数
     *
     * @param functionName 函数名称
     * @throws InterruptedException 异常
     */
    private void transportFunction(String functionName) throws InterruptedException {
        this.checkInterrupt();
        // 删除函数
        this.targetClient.dropFunction(this.targetDatabase, functionName);
        this.message("Drop Function " + functionName);
        this.processedIncr();

        // 创建函数
        MongoFunction function = this.sourceClient.selectFunction(this.sourceDatabase, functionName);
        this.targetClient.createFunction(this.targetDatabase, functionName, function.getCode());
        this.message("Create Function " + functionName);
        this.processedIncr();
    }

    @Override
    protected void doBatchInsert(List<MongoRecord> sqlList) {
        try {
            for (MongoRecord record : sqlList) {
                for (MongoColumn column : record.getColumns()) {
                    column.setDbName(this.getTargetDatabase());
                }
            }
            List<BsonValue> result = this.targetClient.insertCollectionRecord(sqlList);
            this.processedIncr(result.size());
        } catch (Exception ex) {
            this.processedDecr(sqlList.size());
            throw ex;
        }
    }

    /**
     * 设置函数列表
     *
     * @param functions 函数列表
     */
    public void setFunctions(List<ShellMongoDataTransportFunction> functions) {
        this.functions = functions;
    }

    /**
     * 获取函数列表
     *
     * @return 函数列表
     */
    public List<ShellMongoDataTransportFunction> getFunctions() {
        return functions;
    }

    /**
     * 获取来源客户端
     *
     * @return 来源客户端
     */
    public MongoClient getSourceClient() {
        return sourceClient;
    }

    /**
     * 设置来源客户端
     *
     * @param sourceClient 来源客户端
     */
    public void setSourceClient(MongoClient sourceClient) {
        this.sourceClient = sourceClient;
    }

    /**
     * 获取目标客户端
     *
     * @return 目标客户端
     */
    public MongoClient getTargetClient() {
        return targetClient;
    }

    /**
     * 设置目标客户端
     *
     * @param targetClient 目标客户端
     */
    public void setTargetClient(MongoClient targetClient) {
        this.targetClient = targetClient;
    }

    /**
     * 获取表列表
     *
     * @return 表列表
     */
    public List<ShellMongoDataTransportCollection> getTables() {
        return tables;
    }

    /**
     * 设置表列表
     *
     * @param tables 表列表
     */
    public void setTables(List<ShellMongoDataTransportCollection> tables) {
        this.tables = tables;
    }

}

