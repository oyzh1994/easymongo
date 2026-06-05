package cn.oyzh.easymongo.data.handler;


import cn.oyzh.easymongo.mongo.MongoClient;

/**
 *
 * @author oyzh
 * @since 2025-11-26
 */
public class ShellMysqlDataExportHandler extends DBDataExportHandler {

    public ShellMysqlDataExportHandler(MongoClient dbClient, String dbName) {
        super(dbClient, dbName);
    }
}
