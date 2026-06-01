package cn.oyzh.easymongo.trees;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.easymongo.controller.connect.MongoConnectAddController;
import cn.oyzh.easymongo.event.connect.DBAddConnectEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectAddedEvent;
import cn.oyzh.easymongo.event.connect.MongoConnectUpdatedEvent;
import cn.oyzh.easymongo.event.group.MongoAddGroupEvent;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.group.MongoGroupTreeItem;
import cn.oyzh.easymongo.trees.root.MongoRootTreeItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * db树
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoTreeView extends RichTreeView implements FXEventListener {

    /**
     * 搜索中标志位
     */
    private volatile boolean searching;

    @Override
    public MongoTreeItemFilter getItemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            this.itemFilter = new MongoTreeItemFilter();
        }
        return (MongoTreeItemFilter) this.itemFilter;
    }

    public MongoTreeView() {
        this.dragContent = "db_tree_drag";
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.setRoot(new MongoRootTreeItem(this));
        this.root().expend();
    }

    @Override
    public MongoRootTreeItem root() {
        return (MongoRootTreeItem) super.root();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (MongoConnectTreeItem treeItem : this.root().getConnectedItems()) {
            ThreadUtil.start(treeItem::closeConnect);
        }
    }

    /**
     * 连接修改事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onInfoUpdate(MongoConnectUpdatedEvent event) {
        f1:
        for (TreeItem<?> item : this.root().unfilteredChildren()) {
            if (item instanceof MongoConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == event.data()) {
                    connectTreeItem.value(event.data());
                    break;
                }
            } else if (item instanceof MongoGroupTreeItem groupTreeItem) {
                for (MongoConnectTreeItem connectTreeItem : groupTreeItem.getConnectItems()) {
                    if (connectTreeItem.value() == event.data()) {
                        connectTreeItem.value(event.data());
                        break f1;
                    }
                }
            }
        }
    }

    /**
     * 添加连接事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void addConnect(DBAddConnectEvent event) {
        StageManager.showStage(MongoConnectAddController.class, this.window());
    }

    /**
     * 添加分组事件
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addGroup(MongoAddGroupEvent event) {
        this.root().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoAdded(MongoConnectAddedEvent event) {
        this.root().addConnect(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoUpdated(MongoConnectUpdatedEvent event) {
        this.root().infoUpdate(event.data());
    }
}
