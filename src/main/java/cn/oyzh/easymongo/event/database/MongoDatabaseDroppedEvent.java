package cn.oyzh.easymongo.event.database;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class MongoDatabaseDroppedEvent extends Event<MongoDatabaseTreeItem> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据库已删除", this.data().dbName());
    }
}
