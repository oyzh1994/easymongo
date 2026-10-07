package cn.oyzh.easymongo.event.function;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.function.ShellMongoFunctionTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 函数删除事件
 *
 * @author oyzh
 * @since 2024/01/30
 */
public class ShellMongoFunctionDroppedEvent extends Event<ShellMongoFunctionTreeItem> implements EventFormatter {

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String functionName() {
        return this.data().functionName();
    }

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public MongoDatabaseTreeItem getDbItem() {
        return this.data().dbItem();
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] dropped", I18nHelper.function(), this.functionName());
    }
}
