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
 * redis事件工具
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

    public static void databaseClosed(MongoDatabaseTreeItem dbItem) {
        MongoDatabaseClosedEvent event = new MongoDatabaseClosedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void databaseAdded(MongoConnectTreeItem connectItem, MongoDatabase database) {
        MongoDatabaseAddedEvent event = new MongoDatabaseAddedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseUpdated(MongoConnectTreeItem connectItem, MongoDatabase database) {
        MongoDatabaseUpdatedEvent event = new MongoDatabaseUpdatedEvent();
        event.data(database);
        event.setConnectItem(connectItem);
        EventUtil.post(event);
    }

    public static void databaseDropped(MongoDatabaseTreeItem dbItem) {
        MongoDatabaseDroppedEvent event = new MongoDatabaseDroppedEvent();
        event.data(dbItem);
        EventUtil.post(event);
    }

    public static void queryAdd(MongoDatabaseTreeItem item) {
        MongoQueryAddEvent event = new MongoQueryAddEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryAdded(MongoQuery query, MongoDatabaseTreeItem item) {
        MongoQueryAddedEvent event = new MongoQueryAddedEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryDeleted(MongoQueryTreeItem item) {
        MongoQueryDeletedEvent event = new MongoQueryDeletedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void queryOpen(MongoQuery query, MongoDatabaseTreeItem item) {
        MongoQueryOpenEvent event = new MongoQueryOpenEvent();
        event.data(query);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    public static void queryRenamed(String queryId, String queryName, String newQueryName, MongoDatabaseTreeItem item) {
        MongoQueryRenamedEvent event = new MongoQueryRenamedEvent();
        event.data(queryId);
        event.setQueryName(queryName);
        event.setNewQueryName(newQueryName);
        event.setDbItem(item);
        EventUtil.post(event);
    }

    /**
     * 连接已修改事件
     *
     * @param connect DB信息
     */
    public static void connectUpdated(MongoConnect connect) {
        MongoConnectUpdatedEvent event = new MongoConnectUpdatedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void addConnect() {
        EventUtil.post(new MongoAddConnectEvent());
    }

    public static void addGroup() {
        EventUtil.post(new MongoAddGroupEvent());
    }

    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    public static void connectAdded(MongoConnect connect) {
        MongoConnectAddedEvent event = new MongoConnectAddedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void connectDeleted(MongoConnect connect) {
        MongoConnectDeletedEvent event = new MongoConnectDeletedEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    public static void treeItemChanged(TreeItem<?> item) {
        MongoTreeItemChangedEvent event = new MongoTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void collectionDropped(MongoCollectionTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoCollectionDroppedEvent event = new MongoCollectionDroppedEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void collectionOpen(MongoCollectionTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoCollectionOpenEvent event = new MongoCollectionOpenEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void collectionRenamed(String collectionName, String newCollectionName, MongoDatabaseTreeItem dbItem) {
        MongoCollectionRenamedEvent event = new MongoCollectionRenamedEvent();
        event.setDbItem(dbItem);
        event.data(collectionName);
        event.setNewCollectionName(newCollectionName);
        EventUtil.post(event);
    }

    public static void bucketDropped(MongoBucketTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoBucketDroppedEvent event = new MongoBucketDroppedEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void bucketOpen(MongoBucketTreeItem collectionItem, MongoDatabaseTreeItem dbItem) {
        MongoBucketOpenEvent event = new MongoBucketOpenEvent();
        event.data(collectionItem);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     *
     * @param client zk客户端
     */
    public static void terminalOpen(MongoClient client, String dbName) {
        MongoTerminalOpenEvent event = new MongoTerminalOpenEvent();
        event.data(client);
        event.setDbName(dbName);
        EventUtil.post(event);
    }

    public static void dropFunction(ShellMongoFunctionTreeItem treeItem) {
        ShellMongoFunctionDroppedEvent event = new ShellMongoFunctionDroppedEvent();
        event.data(treeItem);
        EventUtil.postSync(event);
    }

    public static void designFunction(MongoFunction function, MongoDatabaseTreeItem dbItem) {
        ShellMongoFunctionDesignEvent event = new ShellMongoFunctionDesignEvent();
        event.data(function);
        event.setDbItem(dbItem);
        EventUtil.post(event);
    }

    public static void functionRenamed(String functionName, String newFunctionName, MongoDatabaseTreeItem dbItem) {
        ShellMongoFunctionRenamedEvent event = new ShellMongoFunctionRenamedEvent();
        event.setDbItem(dbItem);
        event.data(functionName);
        event.setNewFunctionName(newFunctionName);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client redis客户端
     */
    public static void connectionClosed(MongoClient client) {
        MongoConnectionClosedEvent event = new MongoConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client redis客户端
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
