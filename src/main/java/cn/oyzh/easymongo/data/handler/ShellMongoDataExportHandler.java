package cn.oyzh.easymongo.data.handler;


import cn.oyzh.easymongo.mongo.MongoClient;

/**
 * MongoDB 数据导出处理器
 *
 * @author oyzh
 * @since 2025-11-26
 */
public class ShellMongoDataExportHandler extends DBDataExportHandler {

    /**
     * 构造数据导出处理器
     *
     * @param dbClient db 客户端
     * @param dbName   库名称
     */
    public ShellMongoDataExportHandler(MongoClient dbClient, String dbName) {
        super(dbClient, dbName);
    }
}
