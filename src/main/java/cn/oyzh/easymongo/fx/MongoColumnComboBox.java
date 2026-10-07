package cn.oyzh.easymongo.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

import java.util.List;

/**
 * MongoDB字段选择框
 *
 * @author oyzh
 * @since 2024/01/16
 */
public class MongoColumnComboBox extends FXComboBox<MongoColumn> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MongoColumn o) {
                if (o == null) {
                    return "";
                }
                return o.displayName();
            }
        });
    }

    /**
     * 构造字段选择框
     */
    public MongoColumnComboBox() {

    }

    /**
     * 构造字段选择框
     *
     * @param columns 字段列表
     */
    public MongoColumnComboBox(List<MongoColumn> columns) {
        this.addItems(columns);
    }

    /**
     * 根据字段名称选中字段
     *
     * @param colName 字段名称
     */
    public void select(String colName) {
        for (MongoColumn object : this.getItems()) {
            if (StringUtil.equalsIgnoreCase(colName, object.getName())) {
                this.select(object);
                break;
            }
        }
    }

    /**
     * 获取选中字段名称
     *
     * @return 字段名称
     */
    public String getColumnName() {
        return this.getSelectedItem().getName();
    }
}
