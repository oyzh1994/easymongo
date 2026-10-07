package cn.oyzh.easymongo.event.query;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 查询重命名事件
 *
 * @author oyzh
 * @since 2024/01/23
 */
public class MongoQueryRenamedEvent extends Event<String> implements EventFormatter {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 原查询名称
     */
    private String queryName;

    /**
     * 新查询名称
     */
    private String newQueryName;

    /**
     * 获取原查询名称
     *
     * @return 原查询名称
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * 设置原查询名称
     *
     * @param queryName 原查询名称
     */
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * 获取新查询名称
     *
     * @return 新查询名称
     */
    public String getNewQueryName() {
        return newQueryName;
    }

    /**
     * 设置新查询名称
     *
     * @param newQueryName 新查询名称
     */
    public void setNewQueryName(String newQueryName) {
        this.newQueryName = newQueryName;
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
        return String.format("[%s:%s] renamed, new name:%s", I18nHelper.query(), this.getQueryName(), this.getNewQueryName());
    }
}
