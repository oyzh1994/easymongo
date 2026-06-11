package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024/01/23
 */
public class ShellMysqlFunctionRenamedEvent extends Event<String> {

    private MongoDatabaseTreeItem dbItem;

    private String newFunctionName;

    public String getNewFunctionName() {
        return newFunctionName;
    }

    public void setNewFunctionName(String newFunctionName) {
        this.newFunctionName = newFunctionName;
    }

    public String functionName() {
        return this.data();
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
