package cn.oyzh.easymongo.event.query;

import cn.oyzh.event.Event;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;

/**
 * MongoDB 查询打开事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoQueryOpenEvent extends Event<MongoQuery> {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 获取查询标识
     *
     * @return 查询标识
     */
    public String queryId() {
        return this.data().getUid();
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
}
