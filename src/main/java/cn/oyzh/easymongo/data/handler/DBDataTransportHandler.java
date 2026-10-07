package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库数据传输处理器基类
 *
 * @author oyzh
 * @since 2024/09/06
 */
public abstract class DBDataTransportHandler extends DBDataHandler {

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
     * 执行传输
     *
     * @throws Exception 异常
     */
    public abstract void doTransport() throws Exception;

    /**
     * 待批量插入的记录列表
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
     * @param recordList 记录列表
     */
    protected abstract void doBatchInsert(List<MongoRecord> recordList);

    /**
     * 获取来源库
     *
     * @return 来源库
     */
    public String getSourceDatabase() {
        return sourceDatabase;
    }

    /**
     * 设置来源库
     *
     * @param sourceDatabase 来源库
     */
    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    /**
     * 获取目标库
     *
     * @return 目标库
     */
    public String getTargetDatabase() {
        return targetDatabase;
    }

    /**
     * 设置目标库
     *
     * @param targetDatabase 目标库
     */
    public void setTargetDatabase(String targetDatabase) {
        this.targetDatabase = targetDatabase;
    }

    /**
     * 获取查询限制
     *
     * @return 查询限制
     */
    public int getSelectLimit() {
        return selectLimit;
    }

    /**
     * 设置查询限制
     *
     * @param selectLimit 查询限制
     */
    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    /**
     * 获取批量限制
     *
     * @return 批量限制
     */
    public int getBatchLimit() {
        return batchLimit;
    }

    /**
     * 设置批量限制
     *
     * @param batchLimit 批量限制
     */
    public void setBatchLimit(int batchLimit) {
        this.batchLimit = batchLimit;
    }
}

