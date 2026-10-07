package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 查询已新增事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryAddedEvent extends Event<MongoQuery> implements EventFormatter {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    /**
     * 设置数据库树节点
     *
     * @param dbItem 数据库树节点
     */
    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] added", I18nHelper.query(), this.data().getName());
    }
}
