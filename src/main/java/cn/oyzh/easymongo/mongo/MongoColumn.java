package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.util.MongoUtil;
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
     * 集合名称
     */
    private String collectionName;

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

    /**
     * 别名，优先name显示
     */
    private String aliasName;

    public MongoColumn() {

    }

    public MongoColumn(String name) {
        this.name = name;
    }

    public MongoColumn(String name, String aliasName) {
        this.name = name;
        this.aliasName = aliasName;
    }

    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void setType(String type) {
        type = StringUtil.toUpperCase(type);
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
     * 是否支持小数
     *
     * @return 结果
     */
    public boolean supportDigits() {
        return StringUtil.equalsIgnoreCase(this.getType(), "double");
    }

    /**
     * 是否支持整数
     *
     * @return 结果
     */
    public boolean supportInteger() {
        return StringUtil.equalsIgnoreCase(this.getType(), "int");
    }

    public boolean supportString() {
        return StringUtil.equalsIgnoreCase(this.getType(), "string");
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
            this.setCollectionName(column.collectionName);
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

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
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

    public boolean is_id() {
        return MongoUtil.ID.equalsIgnoreCase(this.name);
    }

    public Object defaultValue() {
        if (this.supportInteger()) {
            return 0;
        }
        if (this.supportDigits()) {
            return 0d;
        }
        return "";
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String displayName() {
        return this.aliasName == null ? this.name : this.aliasName;
    }
}
