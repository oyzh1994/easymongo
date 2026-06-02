package cn.oyzh.easymongo.trees.collection;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordData;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.mongo.MysqlSelectRecordParam;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * db树表节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoCollectionTreeItem extends MongoTreeItem<MongoCollectionTreeItemValue> {

    /**
     * 当前值
     */
    private final MongoCollection value;

    public MongoCollectionTreeItem(MongoCollection table, RichTreeView treeView) {
        super(treeView);
        this.value = table;
        this.setValue(new MongoCollectionTreeItemValue(this));
    }

    @Override
    public MongoCollectionsTreeItem parent() {
        return (MongoCollectionsTreeItem) super.parent();
    }

    public MongoClient client() {
        return this.parent().client();
    }

    public String dbName() {
        return this.parent().dbName();
    }

    public String tableName() {
        return this.value.getName();
    }

    /**
     * 获取redis信息
     *
     * @return redis信息
     */
    public MongoConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openTable = MenuItemHelper.openTable("12", this::onPrimaryDoubleClick);
        items.add(openTable);
        FXMenuItem updateTable = MenuItemHelper.designTable("12", this::designTable);
        items.add(updateTable);
        FXMenuItem renameTable = MenuItemHelper.renameTable("12", this::rename);
        items.add(renameTable);
        FXMenuItem clearTable = MenuItemHelper.clearTableData("12", this::clearTableData);
        items.add(clearTable);
        FXMenuItem truncateTable = MenuItemHelper.truncateTable("12", this::truncateTable);
        items.add(truncateTable);
        FXMenuItem dropTable = MenuItemHelper.deleteTable("12", this::delete);
        items.add(dropTable);
        items.add(MenuItemHelper.separator());
        FXMenuItem dumpTable = MenuItemHelper.dumpData("12", this::dump);
        items.add(dumpTable);
        FXMenuItem exportTable = MenuItemHelper.exportData("12", this::export);
        items.add(exportTable);
        FXMenuItem tableInfo = MenuItemHelper.tableInfo("12", this::tableInfo);
        items.add(tableInfo);

        return items;
    }

    /**
     * 转储
     */
    private void dump() {
    }

    /**
     * 导出
     */
    private void export() {
    }

    private void designTable() {
    }

    private void truncateTable() {
    }

    /**
     * 清空表
     */
    private void clearTableData() {
    }

    @Override
    public void delete() {
        try {
            if (MessageBox.confirm(I18nHelper.deleteTable() + "[" + this.tableName() + "]")) {
                this.dbItem().dropCollection(this.tableName());
                MongoEventUtil.collectionDropped(this, this.dbItem());
                this.remove();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    private void tableInfo() {
//        StageAdapter fxView = StageManager.parseStage(MysqlTableInfoController.class, this.window());
//        fxView.setProp("tableItem", this);
//        fxView.display();
    }

    @Override
    public void rename() {
//        try {
//            // if (!MessageBox.confirm(DBI18nHelper.tableTip2())) {
//            //     return;
//            // }
//            String tableName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.value.getName());
//            // 名称为null或者跟当前名称相同，则忽略
//            if (tableName == null || Objects.equals(tableName, this.value.getName())) {
//                return;
//            }
//            // 检查名称
//            if (StringUtil.isBlank(tableName)) {
//                MessageBox.warn(I18nHelper.pleaseInputContent());
//                return;
//            }
//            // if (this.dbItem().existTable(tableName)) {
//            //     MessageBox.warn(I18nHelper.table() + " " + tableName + I18nHelper.alreadyExists());
//            //     return;
//            // }
//            String oldName = this.value.getName();
//            // 修改名称
//            this.dbItem().renameTable(oldName, tableName);
//            this.value.setName(tableName);
//            this.refresh();
//            MysqlEventUtil.tableRenamed(this, this.dbItem());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            MessageBox.exception(ex);
//        }
    }

    public MongoDatabaseTreeItem dbItem() {
        if (this.parent() == null) {
            return null;
        }
        return this.parent().parent();
    }
//
//    public Paging<MongoRecord> recordPage(long pageNo, long limit, List<MongoRecordFilter> filters, List<MysqlColumn> columns) {
//        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
//        param.setLimit(limit);
//        param.setFilters(filters);
//        param.setColumns(columns);
//        param.setDbName(this.dbName());
//        param.setStart(pageNo * limit);
//        param.setTableName(this.tableName());
//        List<MysqlRecord> rows = this.client().selectRecords(param);
//        long count = this.client().selectRecordCount(param);
//        Paging<MysqlRecord> paging = new Paging<>(rows, limit, count);
//        paging.currentPage(pageNo);
//        return paging;
//    }

    public String infoName() {
        return parent().infoName();
    }


    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.collectionOpen(this, this.dbItem());
    }

    @Override
    public void loadChild() {
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    public ObjectId insertRecord(MongoRecordData recordData) {
        return this.client().insertRecord(recordData);
    }

    public long deleteRecord(MongoRecordData recordData) {
        return this.client().deleteRecord(recordData);
    }

    public int updateRecord(MongoRecordData recordData ) {
        return -1;
    }

    public MongoCollection value() {
        return value;
    }

    public Paging<MongoRecord> recordPage(long pageNo, long limit, List<MongoRecordFilter> filters, MongoColumns columns) {
        MysqlSelectRecordParam param = new MysqlSelectRecordParam();
        param.setLimit(limit);
        param.setFilters(filters);
        param.setColumns(columns);
        param.setDbName(this.dbName());
        param.setStart(pageNo * limit);
        param.setCollectionName(this.tableName());
        List<MongoRecord> rows = this.client().selectRecords(param);
        long count = rows.size();
        Paging<MongoRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }
}
