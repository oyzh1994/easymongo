package cn.oyzh.easymongo.event.collection;

import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoCollectionOpenEvent extends Event<MongoCollectionTreeItem> {

    private MongoDatabaseTreeItem dbItem;

    public String tableName() {
        return this.data().tableName();
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
