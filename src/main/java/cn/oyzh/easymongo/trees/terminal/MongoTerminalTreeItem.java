package cn.oyzh.easymongo.trees.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * 终端树节点
 *
 * @author oyzh
 * @since 2023/1/30
 */
public class MongoTerminalTreeItem extends RichTreeItem<MongoTerminalTreeItemValue> {

    /**
     * 构造终端树节点
     *
     * @param treeView 树视图
     */
    public MongoTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new MongoTerminalTreeItemValue());
    }

    /**
     * 获取父节点
     *
     * @return 父节点
     */
    public MongoDatabaseTreeItem parent() {
        return (MongoDatabaseTreeItem) super.parent();
    }

    /**
     * 获取shell连接
     *
     * @return shell连接
     */
    public MongoConnect shellConnect() {
        return this.parent().shellConnect();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public MongoClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.terminalOpen(this.client(), this.parent().dbName());
    }

}
