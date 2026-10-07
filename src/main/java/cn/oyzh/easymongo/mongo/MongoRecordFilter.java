package cn.oyzh.easymongo.mongo;

import cn.oyzh.easymongo.fx.MongoColumnComboBox;
import cn.oyzh.easymongo.fx.MongoConditionComboBox;
import cn.oyzh.easymongo.fx.MongoJoinSymbolComboBox;
import cn.oyzh.easymongo.mongo.condition.MongoCondition;
import cn.oyzh.easymongo.mongo.condition.MongoConditionUtil;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.flex.FlexUtil;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * 记录过滤条件
 *
 * @author oyzh
 * @since 2024/06/26
 */
public class MongoRecordFilter {

    /**
     * 值
     */
    private Object value;

    /**
     * 是否已启用
     */
    private boolean enabled = true;

    /**
     * 连接符号
     */
    private String joinSymbol;

    /**
     * 条件
     */
    private MongoCondition condition;

    /**
     * 字段
     */
    private MongoColumn column;

    /**
     * 字段列表
     */
    private List<MongoColumn> columns;

    /**
     * 值组件
     */
    private FXHBox valueBox;

    /**
     * 获取值
     *
     * @return 值
     */
    public Object value() {
        if (this.valueBox == null || this.valueBox.isChildEmpty()) {
            return this.value;
        }
        return this.value = MongoConditionUtil.getNodeVal(this.valueBox.getChildren());
    }

    /**
     * 获取值组件
     *
     * @return 值组件
     */
    public Node getValueControl() {
        this.updateValueControl();
        return this.valueBox;
    }

    /**
     * 更新值组件
     */
    private void updateValueControl() {
        if (this.valueBox == null) {
            this.valueBox = new FXHBox();
            FlexUtil.flexWidth(this.valueBox, "100%");
        }
        List<Node> nodes = MongoConditionUtil.generateNode(this.column, this.condition);
        MongoConditionUtil.setNodeVal(nodes, this.value);
        if (nodes.size() == 1) {
            FlexUtil.flexWidth(nodes.getFirst(), "100% - 10");
            FlexUtil.flexHeight(nodes.getFirst(), "100%");
        } else if (nodes.size() == 2) {
            FlexUtil.flexWidth(nodes.get(0), "50% - 10");
            FlexUtil.flexHeight(nodes.get(0), "100%");
            FlexUtil.flexWidth(nodes.get(1), "50% - 10");
            FlexUtil.flexHeight(nodes.get(1), "100%");
        }
        for (Node node : nodes) {
            if (node instanceof TextField textField) {
                textField.setPromptText(I18nHelper.pleaseInputContent());
            }
            TableViewUtil.selectRowOnMouseClicked(node);
        }
        this.valueBox.setChild(nodes);
    }

    /**
     * 获取字段组件
     *
     * @return 字段组件
     */
    public MongoColumnComboBox getColumnControl() {
        MongoColumnComboBox comboBox = new MongoColumnComboBox(this.columns);
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.column = newValue;
            this.updateValueControl();
        });
        comboBox.selectFirstIfNull(this.column);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取条件组件
     *
     * @return 条件组件
     */
    public MongoConditionComboBox getConditionControl() {
        MongoConditionComboBox comboBox = new MongoConditionComboBox();
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.condition = newValue;
            this.updateValueControl();
        });
        comboBox.selectFirstIfNull(this.condition);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /**
     * 获取启用组件
     *
     * @return 启用组件
     */
    public FXCheckBox getEnabledControl() {
        FXCheckBox checkBox = new FXCheckBox();
        checkBox.setSelected(this.enabled);
        checkBox.selectedChanged((observable, oldValue, newValue) -> this.enabled = newValue);
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    /**
     * 获取连接符组件
     *
     * @return 连接符组件
     */
    public MongoJoinSymbolComboBox getJoinSymbolControl() {
        MongoJoinSymbolComboBox comboBox = new MongoJoinSymbolComboBox();
        comboBox.selectFirstIfNull(this.joinSymbol);
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> this.joinSymbol = newValue);
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        this.setJoinSymbol(comboBox.getSelectedItem());
        return comboBox;
    }

    /**
     * 获取字段名
     *
     * @return 字段名
     */
    public String column() {
        return this.column.getName();
    }

    /**
     * 获取条件
     *
     * @return 条件
     */
    public Bson condition() {
        return this.condition.wrapCondition(this.column(), this.value());
    }

    /**
     * 是否需要条件
     *
     * @return 结果
     */
    public boolean isRequireCondition() {
        return this.condition.isRequireCondition();
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public Object getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 是否已启用
     *
     * @return 是否已启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取连接符号
     *
     * @return 连接符号
     */
    public String getJoinSymbol() {
        return joinSymbol;
    }

    /**
     * 设置连接符号
     *
     * @param joinSymbol 连接符号
     */
    public void setJoinSymbol(String joinSymbol) {
        this.joinSymbol = joinSymbol;
    }

    /**
     * 获取条件
     *
     * @return 条件
     */
    public MongoCondition getCondition() {
        return condition;
    }

    /**
     * 设置条件
     *
     * @param condition 条件
     */
    public void setCondition(MongoCondition condition) {
        this.condition = condition;
    }

    /**
     * 获取字段
     *
     * @return 字段
     */
    public MongoColumn getColumn() {
        return column;
    }

    /**
     * 设置字段
     *
     * @param column 字段
     */
    public void setColumn(MongoColumn column) {
        this.column = column;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public List<MongoColumn> getColumns() {
        return columns;
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void setColumns(List<MongoColumn> columns) {
        this.columns = columns;
    }
}
