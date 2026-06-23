package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryAddedEvent extends Event<MongoQuery> implements EventFormatter {

    private MongoDatabaseTreeItem dbItem;

    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] added", I18nHelper.query(), this.data().getName());
    }
}
