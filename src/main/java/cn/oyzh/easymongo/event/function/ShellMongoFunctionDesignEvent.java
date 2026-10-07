package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;

/**
 * MongoDB 函数设计事件
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class ShellMongoFunctionDesignEvent extends Event<MongoFunction> {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.data().getName();
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
