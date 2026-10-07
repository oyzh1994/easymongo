package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.script.MongoScriptEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据库脚本文件执行处理器基类
 *
 * @author oyzh
 * @since 2024/08/29
 */
public abstract class DBDataRunFileHandler extends DBDataHandler {

    /**
     * 库名称
     */
    protected String dbName;

    /**
     * sql文件
     */
    protected File sqlFile;

    /**
     * db客户端
     */
    protected MongoClient dbClient;

    /**
     * 连接信息
     */
    protected MongoConnect dbInfo;

    /**
     * 插入限制，insertLimit/batchLimit=连接数，mysql默认是151，尽量不要超过连接数
     */
    protected int insertLimit = 500;

    /**
     * 批量限制
     */
    protected int batchLimit = 50;

    /**
     * 遇到错误时继续
     */
    protected boolean continueWithErrors = true;

    /**
     * 脚本引擎
     */
    protected MongoScriptEngine engine;

    /**
     * 构造脚本文件执行处理器
     *
     * @param dbClient db 客户端
     * @param dbName   库名称
     */
    public DBDataRunFileHandler(MongoClient dbClient, String dbName) {
        this.dbClient = dbClient;
        this.dbName = dbName;
        this.engine = dbClient.shellEngine();
        this.engine.db(this.dbName);
    }

    /**
     * 设置sql文件
     *
     * @param sqlFile sql文件
     * @return 当前对象
     */
    public DBDataRunFileHandler sqlFile(File sqlFile) {
        this.sqlFile = sqlFile;
        return this;
    }

    /**
     * 运行文件
     *
     * @throws Exception 异常
     */
    public abstract void runFile() throws Exception;

    /**
     * 待批量执行的插入 sql 列表
     */
    protected List<String> insertList;

    /**
     * 添加插入sql
     *
     * @param sql 插入sql
     * @throws Exception 异常
     */
    protected void addInsertSql(String sql) throws Exception {
        if (StringUtil.isNotBlank(sql)) {
            if (this.insertList == null) {
                this.insertList = new ArrayList<>();
            }
            this.insertList.add(sql);
            if (this.insertList.size() >= this.insertLimit) {
                this.doBatchInsert();
            }
        }
    }

    /**
     * 执行批量插入
     *
     * @throws Exception 异常
     */
    protected void doBatchInsert() throws Exception {
        if (CollectionUtil.isNotEmpty(this.insertList)) {
            try {
                if (this.insertList.size() <= this.batchLimit) {
                    this.doBatchInsert(this.insertList);
                } else {
                    AtomicReference<Exception> exceptionRef = new AtomicReference<>();
                    List<List<String>> lists = CollectionUtil.split(this.insertList, this.batchLimit);
                    List<Runnable> tasks = new ArrayList<>();
                    for (List<String> list : lists) {
                        tasks.add(() -> {
                            try {
                                this.doBatchInsert(list);
                            } catch (Exception ex) {
                                exceptionRef.set(ex);
                            }
                        });
                    }
                    ThreadUtil.submit(tasks);
                    if (exceptionRef.get() != null) {
                        throw exceptionRef.get();
                    }
                }
            } finally {
                this.insertList.clear();
            }
        }
    }

    /**
     * 执行批量插入
     *
     * @param sqlList sql列表
     */
    protected void doBatchInsert(List<String> sqlList) {
        try {
            int result = 0;
            for (String s : sqlList)
                try {
                    Object res = this.engine.eval(s);
                    if (res != null) {
                        result++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            this.processedIncr(result);
        } catch (Exception ex) {
            this.processedDecr(sqlList.size());
            throw ex;
        }
    }

    /**
     * 创建新的处理器
     *
     * @param dbClient db客户端
     * @param dbName   数据库
     * @return DBDataRunFileHandler
     */
    public static DBDataRunFileHandler newHandler(MongoClient dbClient, String dbName) {
        return new ShellMongoDataRunFileHandler(dbClient, dbName);
    }

    /**
     * 获取库名称
     *
     * @return 库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置库名称
     *
     * @param dbName 库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取 sql 文件
     *
     * @return sql 文件
     */
    public File getSqlFile() {
        return sqlFile;
    }

    /**
     * 设置 sql 文件
     *
     * @param sqlFile sql 文件
     */
    public void setSqlFile(File sqlFile) {
        this.sqlFile = sqlFile;
    }

    /**
     * 获取 db 客户端
     *
     * @return db 客户端
     */
    public MongoClient getDbClient() {
        return dbClient;
    }

    /**
     * 设置 db 客户端
     *
     * @param dbClient db 客户端
     */
    public void setDbClient(MongoClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 获取连接信息
     *
     * @return 连接信息
     */
    public MongoConnect getDbInfo() {
        return dbInfo;
    }

    /**
     * 设置连接信息
     *
     * @param dbInfo 连接信息
     * @return 当前对象
     */
    public DBDataRunFileHandler setDbInfo(MongoConnect dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    /**
     * 获取插入限制
     *
     * @return 插入限制
     */
    public int getInsertLimit() {
        return insertLimit;
    }

    /**
     * 设置插入限制
     *
     * @param insertLimit 插入限制
     */
    public void setInsertLimit(int insertLimit) {
        this.insertLimit = insertLimit;
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

    /**
     * 是否在遇到错误时继续执行
     *
     * @return 是否在遇到错误时继续执行
     */
    public boolean isContinueWithErrors() {
        return continueWithErrors;
    }

    /**
     * 设置是否在遇到错误时继续执行
     *
     * @param continueWithErrors 是否在遇到错误时继续执行
     */
    public void setContinueWithErrors(boolean continueWithErrors) {
        this.continueWithErrors = continueWithErrors;
    }
}

