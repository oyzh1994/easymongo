package cn.oyzh.easymongo.event.query;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class MongoQueryRenamedEvent extends Event<MongoQuery> {

    private MongoDatabaseTreeItem dbItem;

    public String queryName() {
        return this.data().getName();
    }

    public String queryId() {
        return this.data().getUid();
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
