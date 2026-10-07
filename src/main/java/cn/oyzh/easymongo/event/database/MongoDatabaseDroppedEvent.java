package cn.oyzh.easymongo.event.database;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 数据库删除事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class MongoDatabaseDroppedEvent extends Event<MongoDatabaseTreeItem> {

    /**
     * 获取事件格式化文本
     *
     * @return 事件格式化文本
     */
    public String eventFormat() {
        return String.format("[%s:%s] deleted", I18nHelper.database(), this.data().dbName());
    }
}
