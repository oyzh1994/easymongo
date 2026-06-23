package cn.oyzh.easymongo.event.database;

import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class MongoDatabaseAddedEvent extends Event<MongoDatabase> implements EventFormatter {

    private MongoConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] added", I18nHelper.database(), this.data().getName());
    }

    public MongoConnectTreeItem getConnectItem() {
        return connectItem;
    }

    public void setConnectItem(MongoConnectTreeItem connectItem) {
        this.connectItem = connectItem;
    }
}
