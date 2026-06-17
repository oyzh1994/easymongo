package cn.oyzh.easymongo.trees.collection;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.mongo.MongoSelectRecordParam;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.util.MongoViewFactory;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import org.bson.BsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        FXMenuItem renameCollection = MenuItemHelper.renameCollection("12", this::rename);
        items.add(renameCollection);
        FXMenuItem clearCollection = MenuItemHelper.clearCollection("12", this::clearCollection);
        items.add(clearCollection);
        FXMenuItem deleteCollection = MenuItemHelper.deleteCollection("12", this::delete);
        items.add(deleteCollection);
        items.add(MenuItemHelper.separator());
        FXMenuItem dumpTable = MenuItemHelper.dumpData("12", this::dump);
        items.add(dumpTable);
        FXMenuItem exportTable = MenuItemHelper.exportData("12", this::export);
        items.add(exportTable);
        return items;
    }

    /**
     * 转储
     */
    private void dump() {
        MongoViewFactory.dumpData(this.client(), this.dbName(), this.collectionName(), 1);
    }

    /**
     * 导出
     */
    private void export() {
        MongoViewFactory.exportData(this.client(), this.dbName(), this.collectionName());
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

    @Override
    public void rename() {
        try {
            String collectionName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.value.getName());
            // 名称为null或者跟当前名称相同，则忽略
            if (collectionName == null || Objects.equals(collectionName, this.value.getName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(collectionName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent());
                return;
            }
            String oldName = this.value.getName();
            // 修改名称
            this.dbItem().renameCollection(oldName, collectionName);
            this.value.setName(collectionName);
            this.refresh();
            MongoEventUtil.collectionRenamed(oldName, collectionName, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    public MongoDatabaseTreeItem dbItem() {
        if (this.parent() == null) {
            return null;
        }
        return this.parent().parent();
    }

    public String infoName() {
        return parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.collectionOpen(this, this.dbItem());
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
        MongoSelectRecordParam param = new MongoSelectRecordParam();
        param.setLimit(limit);
        param.setFilters(filters);
        param.setColumns(columns);
        param.setDbName(this.dbName());
        param.setStart(pageNo * limit);
        param.setCollectionName(this.collectionName());
        List<MongoRecord> rows = this.client().selectCollectionRecords(param);
        long count = this.client().selectCollectionRecordCount(param);
        Paging<MongoRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    public BsonValue insertRecord(MongoRecord record) {
        return this.dbItem().insertCollectionRecord(record);
    }

    public long deleteRecord(MongoRecord record) {
        return this.dbItem().deleteCollectionRecord(record);
    }

    public long updateRecord(MongoRecord record) {
        return this.dbItem().updateCollectionRecord(record);
    }

    public Object eval(String script) throws Exception {
        return this.dbItem().eval(script);
    }

    public MongoRecord selectCollectionRecord(Object id) {
        return this.dbItem().selectCollectionRecord(this.collectionName(), id);
    }
}
