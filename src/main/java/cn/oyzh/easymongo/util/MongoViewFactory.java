package cn.oyzh.easymongo.util;

import cn.oyzh.easymongo.controller.document.MongoBucketDocumentUpdateController;
import cn.oyzh.easymongo.controller.document.MongoBucketDocumentViewController;
import cn.oyzh.easymongo.controller.document.MongoCollectionDocumentAddController;
import cn.oyzh.easymongo.controller.document.MongoCollectionDocumentUpdateController;
import cn.oyzh.easymongo.controller.data.ShellMongoDataDumpController;
import cn.oyzh.easymongo.controller.data.ShellMongoDataExportController;
import cn.oyzh.easymongo.controller.data.ShellMongoDataImportController;
import cn.oyzh.easymongo.controller.data.ShellMongoRunScriptFileController;
import cn.oyzh.easymongo.controller.database.MongoDatabaseAddController;
import cn.oyzh.easymongo.data.ui.ShellMongoDataExportCollection;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;

/**
 * mongo页面工厂
 *
 * @author oyzh
 * @since 2026-06-03
 */
public class MongoViewFactory {

    /**
     * 添加文档
     *
     * @param columns 字段列表
     * @return 页面
     */
    public static StageAdapter documentAdd(MongoColumns columns) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoCollectionDocumentAddController.class, StageManager.getFrontWindow());
            adapter.setProp("columns", columns);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 编辑文档
     *
     * @param record 记录
     * @return 页面
     */
    public static StageAdapter documentUpdate(MongoRecord record) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoCollectionDocumentUpdateController.class, StageManager.getFrontWindow());
            adapter.setProp("document", record);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 编辑存储桶文档
     *
     * @param record 记录
     * @return 页面
     */
    public static StageAdapter bucketDocumentUpdate(MongoRecord record) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoBucketDocumentUpdateController.class, StageManager.getFrontWindow());
            adapter.setProp("document", record);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 添加数据库
     *
     * @return 页面
     */
    public static StageAdapter databaseAdd(MongoConnectTreeItem treeItem) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoDatabaseAddController.class, treeItem.window());
            adapter.setProp("connectItem", treeItem);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 导出数据
     *
     * @param client    客户端
     * @param collectionName    集合名称
     * @param tableName 表名称
     */
    public static void exportData(MongoClient client, String collectionName, String tableName) {
        exportData(client, collectionName, tableName, 0, null);
    }

    /**
     * 导出数据
     *
     * @param client      客户端
     * @param dbName      数据库名称
     * @param collectionName   集合名称
     * @param exportMode  导出模式
     * @param exportCollection 导出集合
     */
    public static void exportData(MongoClient client, String dbName, String collectionName, int exportMode, ShellMongoDataExportCollection exportCollection) {
        try {
            StageAdapter adapter = StageManager.parseStage(ShellMongoDataExportController.class, StageManager.getFrontWindow());
            adapter.setProp("dbName", dbName);
            adapter.setProp("dbClient", client);
            adapter.setProp("collectionName", collectionName);
            adapter.setProp("exportMode", exportMode);
            adapter.setProp("exportTable", exportCollection);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 导入数据
     *
     * @param client 客户端
     * @param dbName 数据库名称
     */
    public static void importData(MongoClient client, String dbName) {
        try {
            StageAdapter adapter = StageManager.parseStage(ShellMongoDataImportController.class, StageManager.getFrontWindow());
            adapter.setProp("dbName", dbName);
            adapter.setProp("dbClient", client);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 转储数据
     *
     * @param client    客户端
     * @param dbName    数据库名称
     * @param tableName 表名称
     * @param dumpType 导出类型 1.库 2.表
     */
    public static void dumpData(MongoClient client, String dbName, String tableName, int dumpType) {
        try {
            StageAdapter adapter = StageManager.parseStage(ShellMongoDataDumpController.class, StageManager.getFrontWindow());
            adapter.setProp("dumpType", dumpType);
            adapter.setProp("dbName", dbName);
            adapter.setProp("dbClient", client);
            adapter.setProp("tableName", tableName);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 运行脚本文件
     *
     * @param client 客户端
     * @param dbName 数据库名称
     */
    public static void runScriptFile(MongoClient client, String dbName) {
        try {
            StageAdapter adapter = StageManager.parseStage(ShellMongoRunScriptFileController.class, StageManager.getFrontWindow());
            adapter.setProp("dbName", dbName);
            adapter.setProp("dbClient", client);
            adapter.display();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 文件查看
     *
     * @param file   文件
     * @param client 文件客户端
     * @param type   类型
     */
    public static void fileView(MongoRecord file, MongoClient client, String type) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoBucketDocumentViewController.class);
            adapter.setProp("file", file);
            adapter.setProp("type", type);
            adapter.setProp("client", client);
            adapter.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

}
