package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.function.ShellMysqlFunctionTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class ShellMysqlFunctionDroppedEvent extends Event<ShellMysqlFunctionTreeItem>   {

    public String functionName() {
        return this.data().functionName();
    }

    public MongoDatabaseTreeItem getDbItem() {
        return this.data().dbItem();
    }
}
