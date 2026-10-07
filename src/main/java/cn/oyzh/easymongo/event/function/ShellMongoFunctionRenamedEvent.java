package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 函数重命名事件
 *
 * @author oyzh
 * @since 2024/01/23
 */
public class ShellMongoFunctionRenamedEvent extends Event<String> implements EventFormatter {

    /**
     * 数据库树节点
     */
    private MongoDatabaseTreeItem dbItem;

    /**
     * 新函数名称
     */
    private String newFunctionName;

    /**
     * 获取新函数名称
     *
     * @return 新函数名称
     */
    public String getNewFunctionName() {
        return newFunctionName;
    }

    /**
     * 设置新函数名称
     *
     * @param newFunctionName 新函数名称
     */
    public void setNewFunctionName(String newFunctionName) {
        this.newFunctionName = newFunctionName;
    }

    /**
     * 获取原函数名称
     *
     * @return 原函数名称
     */
    public String functionName() {
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
        return String.format("[%s:%s] renamed, new name:%s", I18nHelper.function(), this.functionName(), this.getNewFunctionName());
    }
}
