package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * db字段
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class MongoColumn extends DBObjectStatus implements ObjectCopier<MongoColumn> {

    /**
     * 库名称
     */
    private String dbName;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段类型
     */
    private final StringProperty typeProperty = new SimpleStringProperty();

    /**
     * 字段值
     */
    private String value;

    /**
     * 名称
     */
    private String name;

    public MongoColumn() {

    }

    public MongoColumn(String name) {
        this.name = name;
    }

    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void setType(String type) {
        if (type != null) {
            type = type.toUpperCase();
        }
        this.typeProperty.set(type);
        super.putOriginalData("type", type);
    }

    public List<String> getValueList() {
        List<String> valueList = new ArrayList<>();
        if (this.getValue() != null) {
            List<String> list = StringUtil.split(this.getValue(), ",");
            for (String s : list) {
                if (s.startsWith("'") && s.endsWith("'")) {
                    valueList.add(s.substring(1, s.length() - 1));
                } else {
                    valueList.add(s);
                }
            }
        }
        return valueList;
    }

    public void setValue(String value) {
        this.value = value;
        super.putOriginalData("value", value);
    }

    /**
     * 是否支持整数
     *
     * @return 结果
     */
    public boolean supportInteger() {
        return false;
    }

    public boolean supportString() {
        return false;
    }


    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    public boolean isColumnChanged() {
        for (Map.Entry<String, Object> entry : super.originalData().entrySet()) {
            if (!StringUtil.equalsAny(entry.getKey(), "primaryKey", "primaryKeySize")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initStatus() {
        if (this.value == null) {
            this.setValue(null);
        }
    }


    @Override
    public void copy(MongoColumn column) {
        if (column != null) {
            this.setName(column.name);
            this.setType(column.getType());
            this.setValue(column.value);
            this.setDbName(column.dbName);
            this.setTableName(column.tableName);
        }
    }

    public boolean isInvalid() {
        return StringUtil.isBlank(this.getName()) || StringUtil.isBlank(this.getType());
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return typeProperty.get();
    }

    public StringProperty typeProperty() {
        return typeProperty;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public boolean supportDigits() {
        return false;
    }
}
