package cn.oyzh.easymongo.trees.group;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.easymongo.controller.connect.MongoConnectAddController;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoGroup;
import cn.oyzh.easymongo.store.MongoConnectStore;
import cn.oyzh.easymongo.store.MongoGroupStore;
import cn.oyzh.easymongo.trees.MongoConnectManager;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.MongoTreeView;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.root.MongoRootTreeItem;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DB分组树节点
 *
 * @author oyzh
 * @since 2023/05/12
 */
public class MongoGroupTreeItem extends MongoTreeItem<MongoGroupTreeItemValue> implements MongoConnectManager {

    /**
     * 分组对象
     */
    private final MongoGroup value;

    /**
     * DB信息储存
     */
    private final MongoConnectStore connectStore = MongoConnectStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final MongoGroupStore groupStore = MongoGroupStore.INSTANCE;

    public MongoGroupTreeItem(MongoGroup group, MongoTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new MongoGroupTreeItemValue(this));
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(false);
            this.groupStore.update(this.value);
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(true);
            this.groupStore.update(this.value);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect( this::addConnect);
        FXMenuItem renameGroup = MenuItemHelper.renameGroup( this::rename);
        FXMenuItem delGroup = MenuItemHelper.deleteGroup( this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(groupName)) {
            return;
        }
        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(groupName);
        if (this.groupStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        // 修改名称
        if (this.groupStore.update(this.value)) {
            this.refresh();
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip1())) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip2())) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value)) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<MongoConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父节点
            this.parent().addConnectItems(childes);
        }
        // 移除节点
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageAdapter fxView = StageManager.parseStage(MongoConnectAddController.class, this.window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父节点
     *
     * @return 根节点
     */
    public MongoRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (MongoRootTreeItem) treeItem;
    }

    @Override
    public void addConnect( MongoConnect DBInfo) {
        this.addConnectItem(new MongoConnectTreeItem(DBInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem( MongoConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                this.connectStore.update(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems( List<MongoConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem( MongoConnectTreeItem item) {
        // 删除连接
        if (this.connectStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<MongoConnectTreeItem> getConnectItems() {
        List<MongoConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof MongoConnectTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        if (item instanceof MongoConnectTreeItem connectTreeItem) {
            return !Objects.equals(connectTreeItem.value().getGroupId(), this.value.getGid());
        }
        return false;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof MongoConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }

    public MongoGroup value() {
        return value;
    }
}
