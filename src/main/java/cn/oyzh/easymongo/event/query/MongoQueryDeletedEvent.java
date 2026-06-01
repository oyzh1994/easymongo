package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.trees.query.MongoQueryTreeItem;

/**
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryDeletedEvent extends Event<MongoQueryTreeItem> {

    public String queryId() {
        return this.data().value().getUid();
    }
}
