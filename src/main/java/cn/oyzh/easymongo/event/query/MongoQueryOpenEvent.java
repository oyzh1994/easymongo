package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryOpenEvent extends Event<MongoQuery> {

    private MongoDatabaseTreeItem dbItem;

    public String queryId() {
        return this.data().getUid();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
