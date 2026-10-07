package cn.oyzh.easymongo.data.handler;


import cn.oyzh.easymongo.mongo.MongoClient;

/**
 * MongoDB 数据导入处理器
 *
 * @author oyzh
 * @since 2025-11-26
 */
public class ShellMongoDataImportHandler extends DBDataImportHandler {

    /**
     * 构造数据导入处理器
     *
     * @param dbClient db 客户端
     * @param dbName   库名称
     */
    public ShellMongoDataImportHandler(MongoClient dbClient, String dbName) {
        super(dbClient, dbName);
    }
}
