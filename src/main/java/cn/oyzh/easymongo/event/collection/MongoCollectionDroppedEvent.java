package cn.oyzh.easymongo.event.collection;

import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 集合删除事件
 *
 * @author oyzh
 * @since 2024/01/24
 */
public class MongoCollectionDroppedEvent extends Event<MongoCollectionTreeItem> implements EventFormatter {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String collectionName() {
        return this.data().collectionName();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.dbItem.dbName();
    }

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
        return String.format("[%s:%s] dropped", I18nHelper.collection(), this.collectionName());
    }
}
