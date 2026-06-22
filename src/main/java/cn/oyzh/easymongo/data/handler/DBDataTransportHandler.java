package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.ui.ShellMongoDataTransportCollection;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoRecord;
import org.bson.BsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/06
 */
public abstract class DBDataTransportHandler extends DBDataHandler {

    /**
     * 来源客户端
     */
    protected MongoClient sourceClient;

    /**
     * 目标客户端
     */
    protected MongoClient targetClient;

    /**
     * 来源库
     */
    protected String sourceDatabase;

    /**
     * 目标库
     */
    protected String targetDatabase;

    /**
     * 查询限制，selectLimit/batchLimit=连接数，mysql默认是151，尽量不要超过连接数
     */
    protected int selectLimit = 500;

    /**
     * 批量限制
     */
    protected int batchLimit = 50;

    /**
     * 表
     */
    protected List<ShellMongoDataTransportCollection> tables;

    /**
     * 执行传输
     */
    public abstract void doTransport() throws Exception;

    /**
     * 插入集合
     */
    protected List<MongoRecord> insertList;

    /**
     * 添加插入sql
     *
     * @param sqlList sql列表
     */
    protected void addInsertSql(List<MongoRecord> sqlList) {
        if (CollectionUtil.isNotEmpty(sqlList)) {
            if (this.insertList == null) {
                this.insertList = new ArrayList<>();
            }
            this.insertList.addAll(sqlList);
            if (this.insertList.size() >= this.batchLimit) {
                this.doBatchInsert();
            }
        }
    }

    /**
     * 执行批量插入
     */
    protected void doBatchInsert() {
        if (CollectionUtil.isNotEmpty(this.insertList)) {
            try {
                if (this.insertList.size() <= this.batchLimit) {
                    this.doBatchInsert(this.insertList);
                } else {
                    List<List<MongoRecord>> lists = CollectionUtil.split(this.insertList, this.batchLimit);
                    List<Runnable> tasks = new ArrayList<>();
                    for (List<MongoRecord> list : lists) {
                        tasks.add(() -> this.doBatchInsert(list));
                    }
                    ThreadUtil.submit(tasks);
                }
            } finally {
                this.insertList.clear();
            }
        }
    }

    /**
     * 执行批量插入
     *
     * @param sqlList  sql列表
     */
    protected void doBatchInsert(List<MongoRecord> sqlList ) {
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
     * 创建新的处理器
     *
     * @return DBDataTransportHandler
     */
    public static DBDataTransportHandler newHandler( ) {
         return new ShellMongoDataTransportHandler();
    }

    public MongoClient getSourceClient() {
        return sourceClient;
    }

    public void setSourceClient(MongoClient sourceClient) {
        this.sourceClient = sourceClient;
    }

    public MongoClient getTargetClient() {
        return targetClient;
    }

    public void setTargetClient(MongoClient targetClient) {
        this.targetClient = targetClient;
    }

    public String getSourceDatabase() {
        return sourceDatabase;
    }

    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public String getTargetDatabase() {
        return targetDatabase;
    }

    public void setTargetDatabase(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }

    public List<ShellMongoDataTransportCollection> getTables() {
        return tables;
    }

    public void setTables(List<ShellMongoDataTransportCollection> tables) {
        this.tables = tables;
    }
}

