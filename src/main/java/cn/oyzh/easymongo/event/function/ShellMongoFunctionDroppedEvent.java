package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.function.ShellMongoFunctionTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class ShellMongoFunctionDroppedEvent extends Event<ShellMongoFunctionTreeItem>   {

    public String functionName() {
        return this.data().functionName();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return this.data().dbItem();
    }
}
