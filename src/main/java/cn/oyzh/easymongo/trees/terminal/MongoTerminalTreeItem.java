package cn.oyzh.easymongo.trees.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class MongoTerminalTreeItem extends RichTreeItem<MongoTerminalTreeItemValue> {

    public MongoTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new MongoTerminalTreeItemValue());
    }

    public MongoDatabaseTreeItem parent() {
        return (MongoDatabaseTreeItem) super.parent();
    }

    public MongoConnect shellConnect() {
        return this.parent().shellConnect();
    }

    public MongoClient client() {
        return this.parent().client();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.terminalOpen(this.client(), this.parent().dbName());
    }

}
