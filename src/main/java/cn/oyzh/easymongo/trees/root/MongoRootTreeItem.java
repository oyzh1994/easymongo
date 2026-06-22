package cn.oyzh.easymongo.trees.root;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileChooserHelper;
import cn.oyzh.fx.plus.chooser.FileExtensionFilter;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
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
import cn.oyzh.easymongo.trees.group.MongoGroupTreeItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * DB树根节点
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class MongoRootTreeItem extends MongoTreeItem<MongoRootTreeItemValue> implements MongoConnectManager {

    /**
     * DB信息储存
     */
    private final MongoConnectStore connectStore = MongoConnectStore.INSTANCE;

    /**
     * DB分组储存
     */
    private final MongoGroupStore groupStore = MongoGroupStore.INSTANCE;

    public MongoRootTreeItem(MongoTreeView treeView) {
        super(treeView);
        this.setValue(new MongoRootTreeItemValue());
        // 初始化子节点
        this.initChildes();
    }

    /**
     * 初始化子节点
     */
    private void initChildes() {
        // 初始化分组
        List<MongoGroup> groups = this.groupStore.load();
        if (CollectionUtil.isNotEmpty(groups)) {
            List<TreeItem<?>> list = new ArrayList<>();
            for (MongoGroup group : groups) {
                list.add(new MongoGroupTreeItem(group, this.getTreeView()));
            }
            this.addChild(list);
        }
        // 初始化连接
        List<MongoConnect> infos = this.connectStore.load();
        if (CollectionUtil.isNotEmpty(infos)) {
            this.addConnects(infos);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect( this::addConnect);
        FXMenuItem exportConnect = MenuItemHelper.exportConnect( this::exportConnect);
        FXMenuItem importConnect = MenuItemHelper.importConnect( this::importConnect);
        FXMenuItem addGroup = MenuItemHelper.addGroup( this::addGroup);

        exportConnect.setDisable(this.isChildEmpty());

        items.add(addConnect);
        items.add(exportConnect);
        items.add(importConnect);
        items.add(addGroup);
        return items;
    }

    /**
     * 导出连接
     */
    public void exportConnect() {
        List<MongoConnect> infos = this.connectStore.load();
        if (infos.isEmpty()) {
            MessageBox.warn(I18nHelper.connectionIsEmpty());
            return;
        }
    }

    /**
     * 拖拽文件
     *
     * @param files 文件
     */
    public void dragFile(List<File> files) {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nHelper.onlySupportSingleFile());
            return;
        }
        File file = CollectionUtil.getFirst(files);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 导入连接
     */
    public void importConnect() {
        FileExtensionFilter filter1 = FXChooser.jsonExtensionFilter();
        File file = FileChooserHelper.choose(I18nHelper.chooseFile(), filter1);
        // 解析文件
        this.parseConnect(file);
    }

    /**
     * 解析连接文件
     *
     * @param file 文件
     */
    private void parseConnect(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn(I18nHelper.fileNotExists());
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nHelper.notSupportFolder());
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "json")) {
            MessageBox.warn(I18nHelper.invalidFormat());
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageManager.showStage(MongoConnectAddController.class, this.window());
    }

    /**
     * 添加分组
     */
    public void addGroup() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1());

        // 名称为null，则忽略
        if (groupName == null) {
            return;
        }

        // 不能为空
        if (StringUtil.isBlank(groupName)) {
            MessageBox.warn(I18nHelper.nameCanNotEmpty());
            return;
        }

        MongoGroup group = new MongoGroup();
        group.setName(groupName);
        if (this.groupStore.exist(group)) {
            MessageBox.warn(I18nHelper.contentAlreadyExists());
            return;
        }
        if (this.groupStore.insert(group)) {
            this.addChild(new MongoGroupTreeItem(group, this.getTreeView()));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 获取分组树节点组件
     *
     * @param groupId 分组id
     */
    private MongoGroupTreeItem getGroupItem(String groupId) {
        if (StringUtil.isNotBlank(groupId)) {
            List<MongoGroupTreeItem> items = this.getGroupItems();
            Optional<MongoGroupTreeItem> groupTreeItem = items.parallelStream().filter(g -> Objects.equals(g.value().getGid(), groupId)).findAny();
            return groupTreeItem.orElse(null);
        }
        return null;
    }

    /**
     * 获取分组树节点组件
     *
     * @return 分组树节点组件
     */
    private List<MongoGroupTreeItem> getGroupItems() {
        List<MongoGroupTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof MongoGroupTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    /**
     * 连接新增事件
     *
     * @param info 连接
     */
    public void infoAdd(MongoConnect info) {
        this.addConnect(info);
    }

    /**
     * 连接变更事件
     *
     * @param info 连接
     */
    public void infoUpdate(MongoConnect info) {
        f1:
        for (TreeItem<?> item : this.richChildren()) {
            if (item instanceof MongoConnectTreeItem connectTreeItem) {
                if (connectTreeItem.value() == info) {
                    connectTreeItem.value(info);
                    break;
                }
            } else if (item instanceof MongoGroupTreeItem groupTreeItem) {
                for (MongoConnectTreeItem connectTreeItem : groupTreeItem.getConnectedItems()) {
                    if (connectTreeItem.value() == info) {
                        connectTreeItem.value(info);
                        break f1;
                    }
                }
            }
        }
    }

    @Override
    public void addConnect( MongoConnect info) {
        MongoGroupTreeItem groupItem = this.getGroupItem(info.getGroupId());
        if (groupItem == null) {
            super.addChild(new MongoConnectTreeItem(info, this.getTreeView()));
        } else {
            groupItem.addConnect(info);
        }
    }

    @Override
    public void addConnectItem( MongoConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (item.value().getGroupId() != null) {
                item.value().setGroupId(null);
                this.connectStore.update(item.value());
            }
            super.addChild(item);
            this.expend();
        }
    }

    @Override
    public void addConnectItems( List<MongoConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
            this.expend();
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
        for (TreeItem<?> child : this.richChildren()) {
            if (child instanceof MongoConnectTreeItem connectTreeItem) {
                items.add(connectTreeItem);
            } else if (child instanceof MongoGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectItems());
            }
        }
        return items;
    }

    @Override
    public List<MongoConnectTreeItem> getConnectedItems() {
        List<MongoConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (Object item : this.richChildren()) {
            if (item instanceof MongoConnectTreeItem connectTreeItem) {
                if (connectTreeItem.isConnected()) {
                    items.add(connectTreeItem);
                }
            } else if (item instanceof MongoGroupTreeItem groupTreeItem) {
                items.addAll(groupTreeItem.getConnectedItems());
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
        return item instanceof MongoConnectTreeItem;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof MongoConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}
