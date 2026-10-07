package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.trees.query.MongoQueryTreeItem;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 查询删除事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryDeletedEvent extends Event<MongoQueryTreeItem>  implements EventFormatter {

    /**
     * 获取查询标识
     *
     * @return 查询标识
     */
    public String queryId() {
        return this.data().value().getUid();
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] deleted", I18nHelper.query(), this.data().queryName());
    }
}
