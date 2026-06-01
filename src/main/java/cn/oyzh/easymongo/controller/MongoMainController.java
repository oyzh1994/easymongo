package cn.oyzh.easymongo.controller;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.easymongo.controller.main.ConnectController;
import cn.oyzh.easymongo.controller.main.MessageController;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoSetting;
import cn.oyzh.easymongo.event.connect.MongoConnectUpdatedEvent;
import cn.oyzh.easymongo.event.tree.MongoTreeItemChangedEvent;
import cn.oyzh.easymongo.store.MongoSettingStore;
import cn.oyzh.easymongo.tabs.MongoTabPane;
import cn.oyzh.easymongo.trees.MongoTreeView;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.query.MongoQueriesTreeItem;
import cn.oyzh.easymongo.trees.query.MongoQueryTreeItem;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * db主页
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class MongoMainController extends ParentStageController {

    /**
     * 配置对象
     */
    private final MongoSetting setting = MongoSettingStore.SETTING;

    /**
     * 当前激活的db信息
     */
    private MongoConnect info;

    /**
     * 左侧db树
     */
    @FXML
    public MongoTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FXTabPane tabPaneLeft;

    /**
     * db切换面板
     */
    @FXML
    public MongoTabPane tabPane;

    /**
     * redis连接
     */
    @FXML
    private ConnectController connectController;

    /**
     * redis消息
     */
    @FXML
    private MessageController messageController;

    /**
     * db信息修改事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onInfoUpdate(MongoConnectUpdatedEvent event) {
        if (this.info == event.data()) {
            this.stage.appendTitle(" (" + event.data().getName() + ")");
        }
    }

    /**
     * 刷新窗口标题
     *
     * @param info db信息
     */
    private void flushViewTitle(MongoConnect info) {
        if (info != null) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
        this.info = info;
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
         if (item instanceof MongoQueryTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof MongoDatabaseTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
        } else if (item instanceof MongoConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else {
            this.flushViewTitle(null);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        // 注册事件处理
//        EventUtil.register(this.tree);
//        EventUtil.register(this.tabPane);
//        EventUtil.register(this.msgArea);

        // 初始化过滤
        // this.tree.itemFilter(this.treeItemFilter);
        // this.treeItemFilter.initFilters();
//        this.filter();

//        // 设置上次保存的页面拉伸
//        if (this.setting.isRememberPageResize()) {
//            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
//        }
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        // 取消注册事件处理
//        EventUtil.unregister(this.tree);
//        EventUtil.unregister(this.tabPane);
//        EventUtil.unregister(this.msgArea);
        // 关闭连接
        this.tree.closeConnects();
        // 保存页面拉伸
        this.savePageResize();
        // 取消F5按键监听
        KeyListener.unListenReleased(this.tree, KeyCode.F5);
        KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeMainLeft(Float newWidth) {
        if (newWidth != null && !Double.isNaN(newWidth)) {
            // 设置组件宽
            this.tabPaneLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.tabPaneLeft.parentAutosize();
        }
    }

    @Override
    public void onSystemExit() {
        // 保存页面拉伸
        this.savePageResize();
    }

    /**
     * 保存页面拉伸
     */
    private void savePageResize() {
        if (this.setting.isRememberPageResize()) {
//            this.pageInfo.setMainLeftWidth(this.tabPaneLeft.getMinWidth());
//            this.pageInfoStore.update(this.pageInfo);
        }
    }

    @Override
    protected void bindListeners() {
        // // 左侧栏业务
        // this.onlyCollect.selectedChanged((obs, o, n) -> {
        //     if (n) {
        //         this.showSet.disable();
        //         this.showZSet.disable();
        //         this.showHash.disable();
        //         this.showList.disable();
        //         this.showString.disable();
        //         this.showStream.disable();
        //     } else {
        //         this.showSet.enable();
        //         this.showZSet.enable();
        //         this.showHash.enable();
        //         this.showList.enable();
        //         this.showString.enable();
        //         this.showStream.enable();
        //     }
        //     this.filter();
        // });
        // this.showSet.selectedChanged((obs, o, n) -> this.filter());
        // this.showHash.selectedChanged((obs, o, n) -> this.filter());
        // this.showList.selectedChanged((obs, o, n) -> this.filter());
        // this.showZSet.selectedChanged((obs, o, n) -> this.filter());
        // this.showString.selectedChanged((obs, o, n) -> this.filter());
        // this.showStream.selectedChanged((obs, o, n) -> this.filter());
        // this.sortAsc.managedBindVisible();
        // this.sortDesc.managedBindVisible();
        // // redis树变化事件
        // this.tree.selectItemChanged(this::treeItemChanged);
        // // 文件拖拽初始化
        // this.stage.initDragFile(this.tree.getDragContent(), this.tree.getRoot()::dragFile);
        // 拖动改变redis树大小处理
        // NodeResizer resizeHelper = new NodeResizer(this.tabPaneLeft, Cursor.DEFAULT, this::resizeMainLeft);
        // resizeHelper.widthLimit(240f, 650f);
        // // // 初始化拉伸事件
        // // this.tree.setOnMouseMoved(resizeHelper.mouseMoved());
        // resizeHelper.initResizeEvent();

        // 搜索触发事件
        // KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> RedisEventUtil.searchFire()));
        // // 刷新触发事件
        // KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        // // 刷新触发事件
        // KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 树节点变化事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void treeItemChanged(MongoTreeItemChangedEvent event) {
        if (event.data() instanceof MongoConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else if (event.data() instanceof MongoDatabaseTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else if (event.data() instanceof MongoQueryTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else if (event.data() instanceof MongoQueriesTreeItem treeItem) {
            this.flushViewTitle(treeItem.dbConnect());
        } else {
            this.flushViewTitle(null);
        }
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 布局2
     */
    @EventSubscribe
    private void layout2(Layout2Event event) {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.getRealWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
    }

    /**
     * 布局1
     */
    @EventSubscribe
    private void layout1(Layout1Event event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
    }

    @Override
    public List<SubStageController> getSubControllers() {
        return List.of(this.connectController, this.messageController);
    }
}
