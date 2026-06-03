package cn.oyzh.easymongo.tabs.collection;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoSetting;
import cn.oyzh.easymongo.fx.DBStatusColumn;
import cn.oyzh.easymongo.fx.MongoRecordColumn;
import cn.oyzh.easymongo.fx.MongoRecordTableView;
import cn.oyzh.easymongo.mongo.DBObjectList;
import cn.oyzh.easymongo.mongo.DBStatusListener;
import cn.oyzh.easymongo.mongo.DBStatusListenerManager;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordData;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.popups.MongoCollectionRecordFilterPopupController;
import cn.oyzh.easymongo.popups.MongoPageSettingPopupController;
import cn.oyzh.easymongo.store.MongoSettingStore;
import cn.oyzh.easymongo.trees.collection.MongoCollectionTreeItem;
import cn.oyzh.easymongo.util.MongoRecordUtil;
import cn.oyzh.easymongo.util.MongoViewFactory;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.page.PageEvent;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * db表tab内容组件
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MongoCollectionRecordTabController extends RichTabController {

    /**
     * 根节点
     */
    @FXML
    private FXVBox root;

    /**
     * db树表节点
     */
    private ObjectProperty<MongoCollectionTreeItem> itemProperty;

    /**
     * 分页数据
     */
    private Paging<MongoRecord> pageData;

    /**
     * 记录过滤按钮
     */
    @FXML
    private SVGGlyph filter;

    /**
     * 数据分页组件
     */
    @FXML
    private PageBox<MongoRecord> pageBox;

    /**
     * 数据表单组件
     */
    @FXML
    private MongoRecordTableView recordTable;

    /**
     * 过滤列表
     */
    private List<MongoRecordFilter> filters;

    /**
     * 应用
     */
    @FXML
    private SVGGlyph apply;

    /**
     * 抛弃
     */
    @FXML
    private SVGGlyph discard;

    /**
     * 记录变更监听器
     */
    private DBStatusListener changeListener;

    /**
     * 字段列表
     */
    private MongoColumns columns;

    /**
     * 设置
     */
    private final MongoSetting setting = MongoSettingStore.SETTING;

    /**
     * 执行初始化
     *
     * @param item db树表节点
     */
    public void init(MongoCollectionTreeItem item) {
        this.itemProperty = new SimpleObjectProperty<>(item);
        this.itemProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.closeTab();
            }
        });
        item.parentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.closeTab();
            }
        });
        this.reload();
        if (this.changeListener == null) {
            this.changeListener = new DBStatusListener(this.getItem().dbName() + ":" + this.getItem().collectionName()) {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    apply.enable();
                }
            };
        }
    }

    public MongoCollectionTreeItem getItem() {
        return this.itemProperty.get();
    }

    /**
     * 初始化数据列表
     *
     * @param pageNo 数据页码
     */
    private void initDataList(long pageNo) {
        try {
            this.pageData = this.getItem().recordPage(pageNo, this.setting.getRecordPageLimit(), this.enabledFilters(), this.columns);
            this.pageBox.setPaging(this.pageData);
            List<MongoRecord> records = this.pageData.dataList();
            // 初始化字段
            this.initColumns(records);
            // 初始化数据
            this.initRecords(records);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 获取已启用的表过滤条件
     *
     * @return 已启用的表过滤条件
     */
    private List<MongoRecordFilter> enabledFilters() {
        if (CollectionUtil.isNotEmpty(this.filters)) {
            return this.filters.stream().filter(MongoRecordFilter::isEnabled).toList();
        }
        return null;
    }

    /**
     * 初始化字段
     *
     * @param records 记录列表
     */
    private void initColumns(List<MongoRecord> records) {
        MongoColumns columnList = this.columns;
        if (columnList == null) {
            columnList = new MongoColumns();
        }

        Set<String> colNames = new HashSet<>();
        for (MongoRecord record : records) {
            MongoColumns mongoColumns = record.getColumns();
            for (MongoColumn mongoColumn : mongoColumns) {
                MongoColumn column = columnList.column(mongoColumn.getName());
                if (column == null) {
                    column = new MongoColumn();
                    column.copy(mongoColumn);
                    columnList.add(column);
                }
                colNames.add(mongoColumn.getName());
            }
        }

        List<MongoColumn> delList = new ArrayList<>();
        for (MongoColumn column : columnList) {
            if (!colNames.contains(column.getName())) {
                delList.add(column);
            }
        }

        columnList.removeAll(delList);

        this.initColumns(columnList);
    }

    /**
     * 初始化列
     *
     * @param columns 列数据
     */
    private void initColumns(MongoColumns columns) {
        // 设置字段列表
        this.columns = columns;
        if (this.columns == null) {
            this.recordTable.clearColumn();
            return;
        }
        // 数据列集合
        List<FXTableColumn<MongoRecord, Object>> columnList = new ArrayList<>();
        DBStatusColumn<MongoRecord> statusColumn = new DBStatusColumn<>();
        columnList.add(statusColumn);
        for (MongoColumn column : columns) {
            MongoRecordColumn tableColumn = new MongoRecordColumn(column);
            tableColumn.setPrefWidth(MongoRecordUtil.suitableColumnWidth(column));
            columnList.add(tableColumn);
        }
        this.recordTable.setColumn(columnList);
    }

    /**
     * 初始化记录
     *
     * @param records 数据
     */
    private void initRecords(List<MongoRecord> records) {
        this.recordTable.setItem(records);
    }

    /**
     * 添加记录
     */
    @FXML
    private void addRecord() {
        try {
            MongoRecord lastItem = (MongoRecord) this.recordTable.lastItem();
            if (lastItem == null) {
                this.addDocument();
            } else {
                MongoColumns columns = new MongoColumns(lastItem.getColumns());
                MongoRecord record = new MongoRecord(columns);
                record.setCreated(true);
                for (MongoColumn column : columns) {
                    record.putValue(column, column.defaultValue());
                }
                this.recordTable.addItem(record);
                this.recordTable.selectLast();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 添加文档
     */
    @FXML
    private void addDocument() {
        try {
            StageAdapter adapter = MongoViewFactory.documentAdd(this.columns);
            if (adapter == null) {
                return;
            }
            String doc = adapter.getProp("doc");
            if (StringUtil.isBlank(doc)) {
                return;
            }
            MongoRecord record = MongoRecordUtil.docToRecord(doc, this.getItem().dbName(), this.getItem().collectionName());
            ObjectId _id = this.getItem().insertRecord(record.getRecordData());
            if (_id == null) {
                MessageBox.warn(I18nHelper.addDocumentFail());
                return;
            }
            record.set_id(_id);
            record.clearStatus();
            if (this.recordTable.isItemEmpty()) {
                this.reload();
            } else {
                // 更新字段
                List<MongoRecord> list = new ArrayList<>(this.recordTable.getItems());
                list.add(record);
                this.initColumns(list);
                // 追加内容
                this.recordTable.addItem(record);
                this.recordTable.selectLast();
                this.apply.disable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 插入记录
     *
     * @param record 记录
     */
    private void insertRecord(MongoRecord record) {
        MongoRecordData recordData = record.getRecordData();
        ObjectId _id = this.getItem().insertRecord(recordData);
        record.set_id(_id);
    }

    /**
     * 更改记录
     *
     * @param record 记录
     */
    private void updateRecord(MongoRecord record) {
        // 记录数据
        MongoRecordData recordData = record.getRecordData();
        // 更新行
        long result = this.getItem().updateRecord(recordData);
        // 更新字段
        if (result == 1) {
            this.initColumns(this.recordTable.getItems());
        } else {// 操作失败
            MessageBox.warn(I18nHelper.updateDocumentFail());
        }
    }

    /**
     * 应用变更
     */
    @FXML
    private void apply() {
        if (this.apply.isEnable()) {
            try {
                List<MongoRecord> records = this.recordTable.getItems();
                for (MongoRecord record : records) {
                    if (DBObjectList.isCreated(record)) {
                        this.insertRecord(record);
                        record.clearStatus();
                    } else if (DBObjectList.isChanged(record)) {
                        this.updateRecord(record);
                        record.clearStatus();
                    }
                }
                this.apply.disable();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        }
    }

    /**
     * 丢弃变更
     */
    @FXML
    private void discard() {
        try {
            MongoRecord discardRecord = null;
            for (MongoRecord record : this.recordTable.getItems()) {
                if (record.isCreated()) {
                    discardRecord = record;
                } else if (record.isChanged()) {
                    record.discard();
                }
            }
            this.recordTable.removeItem(discardRecord);
            this.apply.disable();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 刷新记录
     */
    @FXML
    public void reload() {
        StageManager.showMask(this::doReload);
    }

    /**
     * 刷新记录，实际业务
     */
    private void doReload() {
        try {
            // 检查是否有未保存的数据
            if (this.apply.isEnable() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
                return;
            }
            // 初始化数据
            this.initDataList(0);
            // 设置过滤激活
            this.filter.setActive(CollectionUtil.isNotEmpty(this.enabledFilters()));
            // 禁用组件
            this.apply.disable();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 过滤记录
     */
    @FXML
    private void filter() {
        try {
            PopupAdapter popup = PopupManager.parsePopup(MongoCollectionRecordFilterPopupController.class);
            popup.setProp("item", this.getItem());
            popup.setProp("filters", this.filters);
            popup.setProp("columns", this.columns);
            popup.showPopup(this.filter);
            popup.setSubmitHandler(filters -> {
                this.setFilters((List<MongoRecordFilter>) filters);
                this.reload();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 下一页
     */
    @FXML
    private void nextPage() {
        this.initDataList(this.pageData.nextPage());
    }

    /**
     * 上一页
     */
    @FXML
    private void prevPage() {
        this.initDataList(this.pageData.prevPage());
    }

    /**
     * 尾页
     */
    @FXML
    private void lastPage() {
        this.initDataList(this.pageData.lastPage());
    }

    /**
     * 首页
     */
    @FXML
    private void firstPage() {
        this.initDataList(0);
    }

    /**
     * 跳页
     */
    @FXML
    private void pageJump(PageEvent.PageJumpEvent event) {
        this.initDataList(event.getPage());
    }

    /**
     * 页码设置
     */
    @FXML
    private void pageSetting() {
        PopupAdapter popup = PopupManager.parsePopup(MongoPageSettingPopupController.class);
        popup.showPopup(this.pageBox.getSettingBtn());
        int limit = this.setting.getRecordPageLimit();
        popup.setSubmitHandler(o -> {
            if (o instanceof Integer l && l != limit) {
                this.firstPage();
            }
        });
    }

    /**
     * 删除记录
     */
    @FXML
    private void deleteRecord() {
        MongoRecord record = this.recordTable.getSelectedItem();
        this.doDeleteRecord(record);
    }

    /**
     * 删除记录
     *
     * @param record 记录
     */
    private void doDeleteRecord(MongoRecord record) {
        try {
            if (record == null) {
                return;
            }
            if (!MessageBox.confirm(I18nHelper.deleteRecord() + "?")) {
                return;
            }
            // 如果是新增的数据，直接删除
            boolean success;
            if (record.isCreated()) {
                success = true;
            } else {
                success = this.getItem().deleteRecord(record.getRecordData()) == 1;
            }
            // 操作成功
            if (success) {
                this.recordTable.removeItem(record);
            } else {// 操作失败
                MessageBox.warnToast(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onTabClosed(Event event) {
        super.onTabClosed(event);
        DBStatusListenerManager.removeListener(this.changeListener);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.discard.disableProperty().bind(this.apply.disableProperty());
        this.apply.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.root, "action2");
            } else {
                NodeGroupUtil.disable(this.root, "action2");
            }
        });
        this.recordTable.getItems().addListener((ListChangeListener<MongoRecord>) c -> {
            if (c.next() && c.wasAdded()) {
                List<? extends MongoRecord> rows = c.getAddedSubList();
                for (MongoRecord row : rows) {
                    if (DBObjectList.isCreated(row)) {
                        this.apply.enable();
                        break;
                    }
                }
            }
        });
        this.recordTable.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.setEditable(true);
            }
            this.recordTable.refresh();
        });
        this.recordTable.setCtrlSAction(this::apply);
        NodeUtil.nodeOnCtrlS(this.root, this::apply);
    }

    public List<MongoRecordFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<MongoRecordFilter> filters) {
        this.filters = filters;
    }
}
