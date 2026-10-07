package cn.oyzh.easymongo.event;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.event.bucket.MongoBucketDroppedEvent;
import cn.oyzh.easymongo.event.bucket.MongoBucketOpenEvent;
import cn.oyzh.easymongo.event.collection.MongoCollectionDroppedEvent;
import cn.oyzh.easymongo.event.collection.MongoCollectionOpenEvent;
import cn.oyzh.easymongo.event.collection.MongoCollectionRenamedEvent;
import cn.oyzh.easymongo.event.connect.MongoAddConnectEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectAddedEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectDeletedEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectUpdatedEvent;
import cn.oyzh.easymongo.event.connection.MongoConnectionClosedEvent;
import cn.oyzh.easymongo.event.connection.MongoConnectionConnectedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseAddedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseClosedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseDroppedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseUpdatedEvent;
import cn.oyzh.easymongo.event.function.ShellMongoFunctionDesignEvent;
import cn.oyzh.easymongo.event.function.ShellMongoFunctionDroppedEvent;
import cn.oyzh.easymongo.event.function.ShellMongoFunctionRenamedEvent;
import cn.oyzh.easymongo.event.group.MongoAddGroupEvent;
import cn.oyzh.easymongo.event.query.MongoQueryAddEvent;
import cn.oyzh.easymongo.event.query.MongoQueryAddedEvent;
import cn.oyzh.easymongo.event.query.MongoQueryDeletedEvent;
import cn.oyzh.easymongo.event.query.MongoQueryOpenEvent;
import cn.oyzh.easymongo.event.query.MongoQueryRenamedEvent;
import cn.oyzh.easymongo.event.terminal.MongoTerminalOpenEvent;
import cn.oyzh.easymongo.event.tree.MongoTreeItemChangedEvent;
import cn.oyzh.easymongo.event.window.ShellShowMessageEvent;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.trees.bucket.MongoBucketTreeItem;
import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.function.ShellMongoFunctionTreeItem;
import cn.oyzh.easymongo.trees.query.MongoQueryTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;

/**
 * MongoDB 事件工具类，负责各类 MongoDB 事件的发布
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class MongoEventUtil {

    /**
     * 布局1
     */
    public static void layout1() {
        EventUtil.post(new Layout1Event());
    }

    /**
     * 布局2
     */
    public static void layout2() {
        EventUtil.post(new Layout2Event());
    }

    /**
     * 发送数据库关闭事件
     *
     * @param dbItem 数据库树节点
     */
    public static void databaseClosed(MongoDatabaseTreeItem dbItem) {
        MongoDatabaseClosedEvent event = new MongoDatabaseClosedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送数据库新增事件
     *
     * @param connectItem 连接树节点
     * @param database    数据库
     */
    public static void databaseAdded(MongoConnectTreeItem connectItem, MongoDatabase database) {
        MongoDatabaseAddedEvent event = new MongoDatabaseAddedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    /**
     * 发送数据库更新事件
     *
     * @param connectItem 连接树节点
     * @param database    数据库
     */
    public static void databaseUpdated(MongoConnectTreeItem connectItem, MongoDatabase database) {
        MongoDatabaseUpdatedEvent event = new MongoDatabaseUpdatedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    /**
     * 发送数据库删除事件
     *
     * @param dbItem 数据库树节点
     */
    public static void databaseDropped(MongoDatabaseTreeItem dbItem) {
        MongoDatabaseDroppedEvent event = new MongoDatabaseDroppedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送查询新增事件
     *
     * @param item 数据库树节点
     */
    public static void queryAdd(MongoDatabaseTreeItem item) {
        MongoQueryAddEvent event = new MongoQueryAddEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 发送查询已新增事件
     *
     * @param query 查询
     * @param item  数据库树节点
     */
    public static void queryAdded(MongoQuery query, MongoDatabaseTreeItem item) {
        MongoQueryAddedEvent event = new MongoQueryAddedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 发送查询删除事件
     *
     * @param item 查询树节点
     */
    public static void queryDeleted(MongoQueryTreeItem item) {
        MongoQueryDeletedEvent event = new MongoQueryDeletedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 发送查询打开事件
     *
     * @param query 查询
     * @param item  数据库树节点
     */
    public static void queryOpen(MongoQuery query, MongoDatabaseTreeItem item) {
        MongoQueryOpenEvent event = new MongoQueryOpenEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 发送查询重命名事件
     *
     * @param queryId      查询标识
     * @param queryName    原查询名称
     * @param newQueryName 新查询名称
     * @param item         数据库树节点
     */
    public static void queryRenamed(String queryId, String queryName, String newQueryName, MongoDatabaseTreeItem item) {
        MongoQueryRenamedEvent event = new MongoQueryRenamedEvent();
        event.data(queryId);
        event.setQueryName(queryName);
        event.setNewQueryName(newQueryName);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 发送连接已修改事件
     *
     * @param connect DB信息
     */
    public static void connectUpdated(MongoConnect connect) {
        MongoConnectUpdatedEvent event = new MongoConnectUpdatedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 发送新增连接事件
     */
    public static void addConnect() {
        EventUtil.post(new MongoAddConnectEvent());
    }

    /**
     * 发送新增分组事件
     */
    public static void addGroup() {
        EventUtil.post(new MongoAddGroupEvent());
    }

    /**
     * 发送更新日志事件
     */
    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    /**
     * 发送连接已新增事件
     *
     * @param connect 连接信息
     */
    public static void connectAdded(MongoConnect connect) {
        MongoConnectAddedEvent event = new MongoConnectAddedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 发送连接已删除事件
     *
     * @param connect 连接信息
     */
    public static void connectDeleted(MongoConnect connect) {
        MongoConnectDeletedEvent event = new MongoConnectDeletedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 发送树节点变更事件
     *
     * @param item 树节点
     */
    public static void treeItemChanged(TreeItem<?> item) {
        MongoTreeItemChangedEvent event = new MongoTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 发送集合删除事件
     *
     * @param collectionItem 集合树节点
     * @param dbItem         数据库树节点
     */
    public static void collectionDropped(MongoCollectionTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoCollectionDroppedEvent event = new MongoCollectionDroppedEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送集合打开事件
     *
     * @param collectionItem 集合树节点
     * @param dbItem         数据库树节点
     */
    public static void collectionOpen(MongoCollectionTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoCollectionOpenEvent event = new MongoCollectionOpenEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送集合重命名事件
     *
     * @param collectionName    原集合名称
     * @param newCollectionName 新集合名称
     * @param dbItem            数据库树节点
     */
    public static void collectionRenamed(String collectionName, String newCollectionName, MongoDatabaseTreeItem dbItem) {
        MongoCollectionRenamedEvent event = new MongoCollectionRenamedEvent();
        event.setDbItem(dbItem);
        event.data(collectionName);
        event.setNewCollectionName(newCollectionName);
        EventUtil.post(event);
    }

    /**
     * 发送桶删除事件
     *
     * @param collectionItem 桶树节点
     * @param dbItem         数据库树节点
     */
    public static void bucketDropped(MongoBucketTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoBucketDroppedEvent event = new MongoBucketDroppedEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送桶打开事件
     *
     * @param collectionItem 桶树节点
     * @param dbItem         数据库树节点
     */
    public static void bucketOpen(MongoBucketTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoBucketOpenEvent event = new MongoBucketOpenEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送终端打开事件
     *
     * @param client Mongo 客户端
     * @param dbName 数据库名称
     */
    public static void terminalOpen(MongoClient client, String dbName) {
        MongoTerminalOpenEvent event = new MongoTerminalOpenEvent();
        event.data(client);
        event.setDbName(dbName);
        EventUtil.post(event);
    }

    /**
     * 发送函数删除事件
     *
     * @param treeItem 函数树节点
     */
    public static void dropFunction(ShellMongoFunctionTreeItem treeItem) {
        ShellMongoFunctionDroppedEvent event = new ShellMongoFunctionDroppedEvent();
        event.data(treeItem);
        EventUtil.postSync(event);
    }

    /**
     * 发送函数设计的事件
     *
     * @param function 函数
     * @param dbItem   数据库树节点
     */
    public static void designFunction(MongoFunction function, MongoDatabaseTreeItem dbItem) {
        ShellMongoFunctionDesignEvent event = new ShellMongoFunctionDesignEvent();
        event.data(function);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 发送函数重命名事件
     *
     * @param functionName    原函数名称
     * @param newFunctionName 新函数名称
     * @param dbItem          数据库树节点
     */
    public static void functionRenamed(String functionName, String newFunctionName, MongoDatabaseTreeItem dbItem) {
        ShellMongoFunctionRenamedEvent event = new ShellMongoFunctionRenamedEvent();
        event.setDbItem(dbItem);
        event.data(functionName);
        event.setNewFunctionName(newFunctionName);
        EventUtil.post(event);
    }

    /**
     * 发送连接关闭事件
     *
     * @param client Mongo 客户端
     */
    public static void connectionClosed(MongoClient client) {
        MongoConnectionClosedEvent event = new MongoConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 发送连接成功事件
     *
     * @param client Mongo 客户端
     */
    public static void connectionConnected(MongoClient client) {
        MongoConnectionConnectedEvent event = new MongoConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 显示消息页面
     */
    public static void showMessage() {
        EventUtil.post(new ShellShowMessageEvent());
    }
}
