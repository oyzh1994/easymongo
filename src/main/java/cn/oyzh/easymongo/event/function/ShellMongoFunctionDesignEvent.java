package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class ShellMongoFunctionDesignEvent extends Event<MongoFunction> {

    private MongoDatabaseTreeItem dbItem;

    public String functionName() {
        return this.data().getName();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return dbItem;
    }

    public void setDbItem(MongoDatabaseTreeItem dbItem) {
        this.dbItem = dbItem;
    }

}
