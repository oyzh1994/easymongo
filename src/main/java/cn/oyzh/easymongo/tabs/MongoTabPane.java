package cn.oyzh.easymongo.tabs;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.event.collection.MongoCollectionOpenEvent;
import cn.oyzh.easymongo.event.bucket.MongoBucketOpenEvent;
import cn.oyzh.easymongo.event.query.MongoQueryAddEvent;
import cn.oyzh.easymongo.tabs.bucket.MongoBucketRecordTab;
import cn.oyzh.easymongo.tabs.collection.MongoCollectionRecordTab;
import cn.oyzh.easymongo.tabs.query.MysqlQueryMainTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tabs.RichTabPane;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.easymongo.tabs.home.MongoHomeTab;
import javafx.scene.control.Tab;

/**
 * db切换面板
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoTabPane extends RichTabPane implements FXEventListener {

//     @Override
//     protected void initTabPane() {
//         super.initTabPane();
//         this.initHomeTab();
//         // 监听tab
//         this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
//             while (c.next()) {
//                 if (c.wasAdded() || c.wasRemoved()) {
//                     TaskManager.startDelay("db:homeTab:flush", this::flushHomeTab, 100);
//                     // if (c.wasAdded()) {
//                     //     TaskManager.startDelay("db:tableTab:flush", this::flushNodeTab, 100);
//                     // }
//                 }
//             }
//         });
// //        new MysqlTabEventListener(this);
//     }

    /**
     * 刷新主页标签
     */
    private void flushHomeTab() {
        if (this.tabsEmpty()) {
            this.initHomeTab();
        } else if (this.tabsSize() > 1) {
            this.closeHomeTab();
        }
    }

    // /**
    //  * 刷新节点标签
    //  */
    // private void flushNodeTab() {
    //     // 获取设置
    //     DBSetting setting = DBSettingStore.SETTING;
    //     // 判断是否需要处理tab限制
    //     if (setting.isTabUnLimit()) {
    //         return;
    //     }
    //     // 获取全部节点tab
    //     List<MysqlTableDataTab> tabs = this.getMysqlTableTabs();
    //     // 数据不满足限制要求，则直接忽略
    //     if (tabs.size() <= setting.getTabLimit()) {
    //         return;
    //     }
    //     // tab处理函数
    //     Consumer<List<MysqlTableDataTab>> func = tabList -> {
    //         // 数据满足限制要求才处理
    //         if (tabList.size() > setting.getTabLimit()) {
    //             // 进行排序
    //             tabList.sort((o1, o2) -> Comparator.comparingLong(MysqlTableDataTab::getOpenedTime).compare(o2, o1));
    //             // 跳过指定数量
    //             List<MysqlTableDataTab> list = tabList.stream().skip(setting.getTabLimit()).toList();
    //             // 移除tab
    //             if (!list.isEmpty()) {
    //                 FXUtil.runLater(() -> this.getTabs().removeAll(list));
    //             }
    //         }
    //     };
    //     // 限制全部连接
    //     if (setting.isAllTabLimitStrategy()) {
    //         func.accept(tabs);
    //     } else if (setting.isSingleTabLimitStrategy()) {// 限制单个连接
    //         // 分组处理
    //         Map<MysqlClient, List<MysqlTableDataTab>> map = new HashMap<>();
    //         // 按分组添加到map
    //         for (MysqlTableDataTab tab : tabs) {
    //             List<MysqlTableDataTab> list = map.computeIfAbsent(tab.client(), k -> new ArrayList<>());
    //             list.add(tab);
    //         }
    //         // 处理值
    //         for (List<MysqlTableDataTab> tabList : map.values()) {
    //             func.accept(tabList);
    //         }
    //     }
    // }

    /**
     * 获取主页tab
     *
     * @return 主页tab
     */
    public MongoHomeTab getHomeTab() {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MongoHomeTab homeTab) {
                return homeTab;
            }
        }
        return null;
    }

    /**
     * 初始化主页tab
     */
    public void initHomeTab() {
        if (this.getHomeTab() == null) {
            super.addTab(new MongoHomeTab());
        }
    }

    /**
     * 关闭主页tab
     */
    public void closeHomeTab() {
        MongoHomeTab homeTab = this.getHomeTab();
        if (homeTab != null) {
            super.removeTab(homeTab);
        }
    }

    // /**
    //  * 连接关闭事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onConnectionClosed(DBConnectionClosedEvent event) {
    //     if (event.isMysqlType()) {
    //         this.removeTab(this.getMysqlTabs());
    //     } else if (event.isMariadbType()) {
    //         this.removeTab(this.getMariadbTabs());
    //     }
    // }

    // // TODO mysql开始
    //
    // /**
    //  * 获取tab列表
    //  *
    //  * @return tab列表
    //  */
    // public List<MongoTab> getMysqlTabs() {
    //     List<MongoTab> list = new ArrayList<>();
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MongoTab tab1) {
    //             list.add(tab1);
    //         }
    //     }
    //     return list;
    // }
    //
    // private List<MongoTab> getMysqlTabs(MongoDatabaseTreeItem dbItem) {
    //     List<MongoTab> list = new ArrayList<>();
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MongoTab tab1 && tab1.dbItem() == dbItem) {
    //             list.add(tab1);
    //         }
    //     }
    //     return list;
    // }
    //
     private MongoCollectionRecordTab getMysqlTableRecordTab(MongoDatabaseTreeItem dbItem, String tableName) {
         for (Tab tab : this.getTabs()) {
             if (tab instanceof MongoCollectionRecordTab tab1 && tab1.dbItem() == dbItem && StringUtil.equals(tableName, tab1.collectionName())) {
                 return tab1;
             }
         }
         return null;
     }

     /**
      * 表打开事件
      *
      * @param event 事件
      */
     @EventSubscribe
     private void onMysqlTableOpen(MongoCollectionOpenEvent event) {
         MongoCollectionRecordTab tab = this.getMysqlTableRecordTab(event.getDbItem(), event.collectionName());
         if (tab == null) {
             tab = new MongoCollectionRecordTab();
             super.addTab(tab);
         }
         // 选中节点
         this.select(tab);
         // 初始化节点
         tab.init(event.data());
     }
    //
    // /**
    //  * 表重命名事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableRenamed(MysqlTableRenamedEvent event) {
    //     MongoBucketRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
    //     if (tab != null) {
    //         tab.flushTitle();
    //     }
    // }
    //
    // /**
    //  * 表清空事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableCleared(MysqlTableClearedEvent event) {
    //     MongoBucketRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
    //     if (tab != null) {
    //         tab.reload();
    //     }
    // }
    //
    // /**
    //  * 表截断事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableTruncated(MysqlTableTruncatedEvent event) {
    //     MongoBucketRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
    //     if (tab != null) {
    //         tab.reload();
    //     }
    // }
    //
    // /**
    //  * 表删除事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableDropped(MongoBucketDroppedEvent event) {
    //     MongoBucketRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.tableName());
    //     if (tab != null) {
    //         tab.closeTab();
    //     }
    // }
    //
    // /**
    //  * 表过滤事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableFiltered(MysqlTableFilteredEvent event) {
    //     MongoBucketRecordTab tableTab = this.getMysqlTableRecordTab(event.dbItem(), event.data());
    //     if (tableTab != null) {
    //         tableTab.setFilters(event.filters());
    //         tableTab.reload();
    //     }
    // }
    //
    // /**
    //  * 表变更事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableAlerted(MysqlTableAlertedEvent event) {
    //     MongoBucketRecordTab tab = this.getMysqlTableRecordTab(event.dbItem(), event.data());
    //     if (tab != null) {
    //         tab.flush();
    //         tab.reload();
    //     }
    // }
    //
    // private MysqlViewRecordTab getMysqlViewRecordTab(MongoDatabaseTreeItem dbItem, String viewName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlViewRecordTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(tab1.viewName(), viewName)) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 视图打开事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlViewOpen(MysqlViewOpenEvent event) {
    //     try {
    //         MysqlViewRecordTab tab = this.getMysqlViewRecordTab(event.dbItem(), event.viewName());
    //         if (tab == null) {
    //             tab = new MysqlViewRecordTab();
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //         tab.init(event.data());
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex);
    //     }
    // }
    //
    // /**
    //  * 视图过滤事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlViewFiltered(MysqlViewFilteredEvent event) {
    //     MysqlViewRecordTab viewRecordTab = this.getMysqlViewRecordTab(event.dbItem(), event.viewName());
    //     if (viewRecordTab != null) {
    //         viewRecordTab.setFilters(event.filters());
    //         viewRecordTab.reload();
    //     }
    // }
    //
    // private MysqlQueryMainTab getMysqlQueryMainTab(String queryId) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlQueryMainTab tab1 && StrUtil.equals(tab1.queryId(), queryId)) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 查询新增事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlQueryAdd(MongoQueryAddEvent event) {
    //     try {
    //         MysqlQueryMainTab tab = new MysqlQueryMainTab();
    //         super.addTab(tab);
    //         this.select(tab);
    //         DBQuery query = new DBQuery();
    //         tab.init(query, event.data());
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // /**
    //  * 查询删除事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlQueryDeleted(MongoQueryDeletedEvent event) {
    //     MysqlQueryMainTab tab = this.getMysqlQueryMainTab(event.data());
    //     if (tab != null) {
    //         super.removeTab(tab);
    //     }
    // }
    //
    // /**
    //  * 查询打开事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlQueryOpen(MongoQueryOpenEvent event) {
    //     try {
    //         MysqlQueryMainTab tab = this.getMysqlQueryMainTab(event.queryId());
    //         if (tab == null) {
    //             tab = new MysqlQueryMainTab();
    //             tab.init(event.data(), event.item());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // /**
    //  * 数据库关闭事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlDatabaseClosed(MongoDatabaseClosedEvent event) {
    //     this.removeTab(this.getMysqlTabs(event.data()));
    // }
    //
    // private MysqlFunctionDesignTab getMysqlFunctionTab(MongoDatabaseTreeItem dbItem, String functionName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlFunctionDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(functionName, tab1.functionName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 函数设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlFunctionDesign(MysqlFunctionDesignEvent event) {
    //     try {
    //         MysqlFunctionDesignTab tab = this.getMysqlFunctionTab(event.dbItem(), event.functionName());
    //         if (tab == null) {
    //             tab = new MysqlFunctionDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // private MysqlProcedureDesignTab getMysqlProcedureTab(MongoDatabaseTreeItem dbItem, String procedureName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlProcedureDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(procedureName, tab1.procedureName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 过程设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlProcedureDesign(MysqlProcedureDesignEvent event) {
    //     try {
    //         MysqlProcedureDesignTab tab = this.getMysqlProcedureTab(event.dbItem(), event.procedureName());
    //         if (tab == null) {
    //             tab = new MysqlProcedureDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // private MysqlEventDesignTab getMysqlEventTab(MongoDatabaseTreeItem dbItem, String eventName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlEventDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(eventName, tab1.eventName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 事件设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlEventDesign(MysqlEventDesignEvent event) {
    //     try {
    //         MysqlEventDesignTab tab = this.getMysqlEventTab(event.dbItem(), event.eventName());
    //         if (tab == null) {
    //             tab = new MysqlEventDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // private MysqlViewDesignTab getMysqlViewDesignTab(MongoDatabaseTreeItem dbItem, String viewName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlViewDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(viewName, tab1.viewName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 视图设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlViewDesign(MysqlViewDesignEvent event) {
    //     try {
    //         MysqlViewDesignTab tab = this.getMysqlViewDesignTab(event.dbItem(), event.viewName());
    //         if (tab == null) {
    //             tab = new MysqlViewDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // private MysqlTableDesignTab getMysqlTableDesignTab(MongoDatabaseTreeItem dbItem, String tableName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MysqlTableDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equalsIgnoreCase(tableName, tab1.tableName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 表设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMysqlTableDesign(MysqlTableDesignEvent event) {
    //     try {
    //         MysqlTableDesignTab tab = this.getMysqlTableDesignTab(event.dbItem(), event.tableName());
    //         if (tab == null) {
    //             tab = new MysqlTableDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    //
    // // TODO mysql结束
    //
    // // TODO mariadb开始
    //
    // /**
    //  * 获取tab列表
    //  *
    //  * @return tab列表
    //  */
    // public List<MariadbTab> getMariadbTabs() {
    //     List<MariadbTab> list = new ArrayList<>();
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbTab tab1) {
    //             list.add(tab1);
    //         }
    //     }
    //     return list;
    // }
    //
    // public List<MariadbTab> getMariadbTabs(MariadbDatabaseTreeItem dbItem) {
    //     List<MariadbTab> list = new ArrayList<>();
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbTableRecordTab tab1 && tab1.dbItem() == dbItem) {
    //             list.add(tab1);
    //         }
    //     }
    //     return list;
    // }
    //
    // private MariadbTableRecordTab getMariadbTableRecordTab(MariadbDatabaseTreeItem dbItem, String tableName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbTableRecordTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(tableName, tab1.tableName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    //
    // /**
    //  * 表过滤事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbTableFiltered(MariadbTableFilteredEvent event) {
    //     MariadbTableRecordTab recordTab = this.getMariadbTableRecordTab(event.dbItem(), event.data());
    //     if (recordTab != null) {
    //         recordTab.setFilters(event.filters());
    //         recordTab.reload();
    //     }
    // }
    //
    // /**
    //  * 表变更事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbTableAlerted(MariadbTableAlertedEvent event) {
    //     MariadbTableRecordTab tab = this.getMariadbTableRecordTab(event.dbItem(), event.data());
    //     if (tab != null) {
    //         tab.flush();
    //         tab.reload();
    //     }
    // }
    //
    // private MariadbFunctionDesignTab getMariadbFunctionTab(MariadbDatabaseTreeItem dbItem, String functionName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbFunctionDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(functionName, tab1.functionName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 函数设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbFunctionDesign(MariadbFunctionDesignEvent event) {
    //     MariadbFunctionDesignTab tab = this.getMariadbFunctionTab(event.dbItem(), event.functionName());
    //     if (tab == null) {
    //         tab = new MariadbFunctionDesignTab();
    //         tab.init(event.data(), event.dbItem());
    //         super.addTab(tab);
    //     }
    //     this.select(tab);
    // }
    //
    // private MariadbProcedureDesignTab getMariadbProcedureTab(MariadbDatabaseTreeItem dbItem, String procedureName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbProcedureDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(procedureName, tab1.procedureName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 过程设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbProcedureDesign(MariadbProcedureDesignEvent event) {
    //     MariadbProcedureDesignTab tab = this.getMariadbProcedureTab(event.dbItem(), event.procedureName());
    //     if (tab == null) {
    //         tab = new MariadbProcedureDesignTab();
    //         tab.init(event.data(), event.dbItem());
    //         super.addTab(tab);
    //     }
    //     this.select(tab);
    // }
    //
    // private MariadbEventDesignTab getMariadbEventTab(MariadbDatabaseTreeItem dbItem, String eventName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbEventDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(eventName, tab1.eventName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 事件设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbEventDesign(MariadbEventDesignEvent event) {
    //     MariadbEventDesignTab tab = this.getMariadbEventTab(event.dbItem(), event.eventName());
    //     if (tab == null) {
    //         tab = new MariadbEventDesignTab();
    //         tab.init(event.data(), event.dbItem());
    //         super.addTab(tab);
    //     }
    //     this.select(tab);
    // }
    //
    // private MariadbViewDesignTab getMariadbViewDesignTab(MariadbDatabaseTreeItem dbItem, String viewName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbViewDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(viewName, tab1.viewName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 视图设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbViewDesign(MariadbViewDesignEvent event) {
    //     MariadbViewDesignTab tab = this.getMariadbViewDesignTab(event.dbItem(), event.viewName());
    //     if (tab == null) {
    //         tab = new MariadbViewDesignTab();
    //         tab.init(event.data(), event.dbItem());
    //         super.addTab(tab);
    //     }
    //     this.select(tab);
    // }
    //
    // private MariadbTableDesignTab getMariadbTableDesignTab(MariadbDatabaseTreeItem dbItem, String tableName) {
    //     for (Tab tab : this.getTabs()) {
    //         if (tab instanceof MariadbTableDesignTab tab1 && tab1.dbItem() == dbItem && StrUtil.equals(tableName, tab1.tableName())) {
    //             return tab1;
    //         }
    //     }
    //     return null;
    // }
    //
    // /**
    //  * 表设计事件
    //  *
    //  * @param event 事件
    //  */
    // @EventSubscribe
    // private void onMariadbTableDesign(MariadbTableDesignEvent event) {
    //     try {
    //         MariadbTableDesignTab tab = this.getMariadbTableDesignTab(event.dbItem(), event.tableName());
    //         if (tab == null) {
    //             tab = new MariadbTableDesignTab();
    //             tab.init(event.data(), event.dbItem());
    //             super.addTab(tab);
    //         }
    //         this.select(tab);
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }
    // // TODO mariadb结束

    // @Override
    // public void initNode() {
    //     super.initNode();
    //    this.setupSelectCountListener();
    // }

    private MongoBucketRecordTab getBucketRecordTab(MongoDatabaseTreeItem dbItem, String bucketName) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof MongoBucketRecordTab tab1 && tab1.dbItem() == dbItem && StringUtil.equals(bucketName, tab1.bucketName())) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 桶打开事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onBucketOpen(MongoBucketOpenEvent event) {
        MongoBucketRecordTab tab = this.getBucketRecordTab(event.getDbItem(), event.bucketName());
        if (tab == null) {
            tab = new MongoBucketRecordTab();
            super.addTab(tab);
        }
        // 选中节点
        this.select(tab);
        // 初始化节点
        tab.init(event.data());
    }

    /**
     * 查询新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onMysqlQueryAdd(MongoQueryAddEvent event) {
        try {
            MysqlQueryMainTab tab = new MysqlQueryMainTab();
            this.addTab(tab);
            this.select(tab);
            MongoQuery query = new MongoQuery();
            tab.init(query, event.data());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
