package cn.oyzh.easymongo.event.database;

import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 数据库更新事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class MongoDatabaseUpdatedEvent extends Event<MongoDatabase> implements EventFormatter {

    /**
     * 连接树节点
     */
    private MongoConnectTreeItem connectItem;

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] updated", I18nHelper.database(), this.data().getName());
    }

    /**
     * 获取连接树节点
     *
     * @return 连接树节点
     */
    public MongoConnectTreeItem getConnectItem() {
        return connectItem;
    }

    /**
     * 设置连接树节点
     *
     * @param connectItem 连接树节点
     */
    public void setConnectItem(MongoConnectTreeItem connectItem) {
        this.connectItem = connectItem;
    }
}
