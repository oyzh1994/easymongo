package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.fx.DBStatusColumn;
import cn.oyzh.easymongo.fx.MongoRecordColumn;
import cn.oyzh.easymongo.fx.MongoRecordTableView;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.query.MysqlExplainResult;
import cn.oyzh.easymongo.util.MongoRecordUtil;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.table.FXTableColumn;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/08/16
 */
public class MysqlQueryExplainTabController extends RichTabController {

    /**
     * sql组件
     */
    @FXML
    private FXText sql;

    /**
     * 耗时组件
     */
    @FXML
    private FXText used;

    /**
     * 计数组件
     */
    @FXML
    private FXText count;

    /**
     * 数据表单组件
     */
    @FXML
    private MongoRecordTableView recordTable;

    /**
     * 执行结果
     */
    private MysqlExplainResult result;

    /**
     * 执行初始化
     *
     * @param result 执行结果
     */
    public void init(MysqlExplainResult result ) {
        this.result = result;
        this.initDataList();
    }

    /**
     * 初始化数据列表
     */
    private void initDataList() {
        try {
            // 初始化字段
            this.initColumns(this.result.columnList());
            // 初始化数据
            this.initRecords(this.result.getRecords());
            // 初始化sql信息
            this.sql.setText(this.result.getSql());
            this.used.setText(I18nHelper.time() + ": " + this.result.getUsedMs() + "ms");
            this.count.setText(I18nHelper.totalData() + ": " + this.result.getCount());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 初始化列
     *
     * @param columns 列数据
     */
    private void initColumns(List<MongoColumn> columns) {
        // 数据列集合
        List<FXTableColumn<MongoRecord, Object>> columnList = new ArrayList<>();
        DBStatusColumn<MongoRecord> statusColumn = new DBStatusColumn<>();
        columnList.add(statusColumn);
        for (MongoColumn column : columns) {
            MongoRecordColumn tableColumn = new MongoRecordColumn(column);
            tableColumn.setRealWidth(MongoRecordUtil.suitableColumnWidth(column));
            columnList.add(tableColumn);
        }
        this.recordTable.getColumns().setAll(columnList);
    }

    /**
     * 初始化记录
     *
     * @param records 数据
     */
    private void initRecords(List<MongoRecord> records) {
        this.recordTable.setItem(records);
    }
}
