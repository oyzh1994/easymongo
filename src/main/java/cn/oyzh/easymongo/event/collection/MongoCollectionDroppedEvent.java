package cn.oyzh.easymongo.event.collection;

import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/24
 */
public class MongoCollectionDroppedEvent extends Event<MongoCollectionTreeItem> {

    private MongoDatabaseTreeItem dbItem;

    public String collectionName() {
        return this.data().collectionName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
