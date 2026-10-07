package cn.oyzh.easymongo.event.bucket;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.bucket.MongoBucketTreeItem;
import cn.oyzh.event.Event;

/**
 * MongoDB 桶打开事件
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoBucketOpenEvent extends Event<MongoBucketTreeItem> {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 获取桶名称
     *
     * @return 桶名称
     */
    public String bucketName() {
        return this.data().bucketName();
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
}
