package cn.oyzh.easymongo.data.handler;

import cn.oyzh.common.date.DateHelper;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.file.FastFileWriter;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 数据库数据转储处理器基类
 *
 * @author oyzh
 * @since 2024/08/22
 */
public abstract class DBDataDumpHandler extends DBDataHandler {

    /**
     * 数据类型
     * 0 数据和结构
     * 1 仅结构
     */
    protected Byte dataType;

    /**
     * 库名称
     */
    protected String dbName;

    /**
     * 转储文件
     */
    protected File dumpFile;

    /**
     * 文件写入器
     */
    protected FastFileWriter fileWriter;

//    /**
//     * db客户端
//     */
//    protected MongoClient dbClient;

    /**
     * 1. 库
     * 2. 集合
     */
    protected Byte dumpType;

    /**
     * 表名称
     */
    protected String tableName;

    /**
     * 连接信息
     */
    protected MongoConnect dbInfo;

    /**
     * 查询限制
     */
    protected int queryLimit = 500;

    /**
     * 构造数据转储处理器
     *
     * @param dbName 库名称
     */
    public DBDataDumpHandler( String dbName) {
        this.dbName = dbName;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     * @return 当前对象
     */
    public DBDataDumpHandler dumpFile(File dumpFile) throws IOException {
        this.dumpFile = dumpFile;
        if (this.fileWriter != null) {
            this.fileWriter.close();
        }
        this.fileWriter = new FastFileWriter(dumpFile);
        return this;
    }

    /**
     * 执行转储
     *
     * @throws Exception 异常
     */
    public abstract void doDump() throws Exception;

    /**
     * 写入头部
     */
    protected abstract void writeHeader() throws IOException;

    /**
     * 写入尾部
     */
    protected void writeTail() throws IOException {
        this.fileWriter.close();
    }

    /**
     * 是否转储数据
     *
     * @return 是否转储数据
     */
    public boolean isDumpRecord() {
        return this.dataType == 0;
    }

    /**
     * 创建新的处理器
     *
     * @param dbClient db客户端
     * @param dbName   数据库
     * @return DBDataDumpHandler
     */
    public static DBDataDumpHandler newHandler(MongoClient dbClient, String dbName) {
        return new ShellMongoDataDumpHandler(dbClient, dbName);
    }

    /**
     * 获取数据类型
     *
     * @return 数据类型
     */
    public Byte getDataType() {
        return dataType;
    }

    /**
     * 设置数据类型
     *
     * @param dataType 数据类型
     */
    public void setDataType(Byte dataType) {
        this.dataType = dataType;
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
     * 获取转储文件
     *
     * @return 转储文件
     */
    public File getDumpFile() {
        return dumpFile;
    }

    /**
     * 设置转储文件
     *
     * @param dumpFile 转储文件
     */
    public void setDumpFile(File dumpFile) {
        this.dumpFile = dumpFile;
    }

    /**
     * 获取文件写入器
     *
     * @return 文件写入器
     */
    public FastFileWriter getFileWriter() {
        return fileWriter;
    }

    /**
     * 设置文件写入器
     *
     * @param fileWriter 文件写入器
     */
    public void setFileWriter(FastFileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    /**
     * 获取转储类型
     *
     * @return 转储类型
     */
    public Byte getDumpType() {
        return dumpType;
    }

    /**
     * 设置转储类型
     *
     * @param dumpType 转储类型
     * @return 当前对象
     */
    public DBDataDumpHandler setDumpType(Byte dumpType) {
        this.dumpType = dumpType;
        return this;
    }

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名称
     *
     * @param tableName 表名称
     * @return 当前对象
     */
    public DBDataDumpHandler setTableName(String tableName) {
        this.tableName = tableName;
        return this;
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
    public DBDataDumpHandler setDbInfo(MongoConnect dbInfo) {
        this.dbInfo = dbInfo;
        return this;
    }

    /**
     * 获取查询限制
     *
     * @return 查询限制
     */
    public int getQueryLimit() {
        return queryLimit;
    }

    /**
     * 设置查询限制
     *
     * @param queryLimit 查询限制
     * @return 当前对象
     */
    public DBDataDumpHandler setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
        return this;
    }
}

