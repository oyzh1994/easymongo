package cn.oyzh.easymongo.event;

import cn.oyzh.easymongo.event.collection.MongoCollectionDroppedEvent;
import cn.oyzh.easymongo.event.collection.MongoCollectionOpenEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseAddedEvent;
import cn.oyzh.easymongo.event.bucket.MongoBucketDroppedEvent;
import cn.oyzh.easymongo.event.bucket.MongoBucketOpenEvent;
import cn.oyzh.easymongo.event.query.MongoQueryDeletedEvent;
import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.bucket.MongoBucketTreeItem;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.event.connect.DBAddConnectEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectAddedEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectDeletedEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectUpdatedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseClosedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseDroppedEvent;
import cn.oyzh.easymongo.event.database.MongoDatabaseUpdatedEvent;
import cn.oyzh.easymongo.event.group.MongoAddGroupEvent;
import cn.oyzh.easymongo.event.query.MongoQueryAddEvent;
import cn.oyzh.easymongo.event.query.MongoQueryAddedEvent;
import cn.oyzh.easymongo.event.query.MongoQueryOpenEvent;
import cn.oyzh.easymongo.event.query.MongoQueryRenamedEvent;
import cn.oyzh.easymongo.event.tree.MongoTreeItemChangedEvent;
import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.query.MongoQueryTreeItem;
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

    public static void queryRenamed(MongoQuery query, MongoDatabaseTreeItem item) {
        MongoQueryRenamedEvent event = new MongoQueryRenamedEvent();
        event.data(query);
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
        EventUtil.post(new DBAddConnectEvent());
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
}
