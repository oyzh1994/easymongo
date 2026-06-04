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

    public String collectionName() {
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
        FXMenuItem openCollection = MenuItemHelper.openCollection("12", this::onPrimaryDoubleClick);
        items.add(openCollection);
        FXMenuItem clearCollection = MenuItemHelper.clearCollection("12", this::clearCollection);
        items.add(clearCollection);
        FXMenuItem deleteCollection = MenuItemHelper.deleteCollection("12", this::delete);
        items.add(deleteCollection);
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

    /**
     * 清空集合
     */
    private void clearCollection() {
        if (MessageBox.confirm(I18nHelper.clearCollection() + "[" + this.collectionName() + "]")) {
            this.dbItem().clearCollection(this.collectionName());
            this.parent().reloadChild();
        }
    }

    @Override
    public void delete() {
        try {
            if (MessageBox.confirm(I18nHelper.deleteCollection() + "[" + this.collectionName() + "]")) {
                this.dbItem().dropCollection(this.collectionName());
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
        param.setCollectionName(this.collectionName());
        List<MongoRecord> rows = this.client().selectBucketRecords(param);
        long count = rows.size();
        Paging<MongoRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    public ObjectId insertRecord(MongoRecordData recordData) {
        return this.client().insertCollectionRecord(recordData);
    }

    public long deleteRecord(MongoRecordData recordData) {
        return this.client().deleteCollectionRecord(recordData);
    }

    public long updateRecord(MongoRecordData recordData ) {
        return this.client().updateCollectionRecord(recordData);
    }
}
