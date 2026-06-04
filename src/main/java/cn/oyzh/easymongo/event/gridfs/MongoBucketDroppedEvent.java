package cn.oyzh.easymongo.event.gridfs;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.gridfs.MongoBucketTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/24
 */
public class MongoBucketDroppedEvent extends Event<MongoBucketTreeItem> {

    private MongoDatabaseTreeItem dbItem;

    public String bucketName() {
        return this.data().bucketName();
    }

    public String dbName() {
        return this.dbItem.dbName();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }
}
