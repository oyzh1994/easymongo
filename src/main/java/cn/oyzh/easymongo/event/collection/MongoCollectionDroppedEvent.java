package cn.oyzh.easymongo.event.collection;

import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2024/01/24
 */
public class MongoCollectionDroppedEvent extends Event<MongoCollectionTreeItem> implements EventFormatter {

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

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] dropped", I18nHelper.collection(), this.collectionName());
    }
}
