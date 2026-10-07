package cn.oyzh.easymongo.event.collection;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 集合重命名事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoCollectionRenamedEvent extends Event<String> implements EventFormatter {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 新集合名称
     */
    private String newCollectionName;

    /**
     * 获取新集合名称
     *
     * @return 新集合名称
     */
    public String getNewCollectionName() {
        return newCollectionName;
    }

    /**
     * 设置新集合名称
     *
     * @param newCollectionName 新集合名称
     */
    public void setNewCollectionName(String newCollectionName) {
        this.newCollectionName = newCollectionName;
    }

    /**
     * 获取原集合名称
     *
     * @return 原集合名称
     */
    public String tableName() {
        return this.data();
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
        return String.format("[%s:%s] renamed, new name:%s", I18nHelper.collection(), this.tableName(), this.getNewCollectionName());
    }

}
