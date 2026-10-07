package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.util.MongoUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

/**
 * 数据库字段
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

    /**
     * 构造数据库字段
     */
    public MongoColumn() {

    }

    /**
     * 使用字段名称构造数据库字段
     *
     * @param name 字段名称
     */
    public MongoColumn(String name) {
        this.name = name;
    }

    /**
     * 使用字段名称、别名构造数据库字段
     *
     * @param name      字段名称
     * @param aliasName 字段别名
     */
    public MongoColumn(String name, String aliasName) {
        this.name = name;
        this.aliasName = aliasName;
    }

    /**
     * 字段名称是否已变更
     *
     * @return 是否已变更
     */
    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    /**
     * 获取字段原始名称
     *
     * @return 字段原始名称
     */
    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    /**
     * 设置字段类型
     *
     * @param type 字段类型
     */
    public void setType(String type) {
        type = StringUtil.toUpperCase(type);
        this.typeProperty.set(type);
        super.putOriginalData("type", type);
    }

    /**
     * 设置字段值
     *
     * @param value 字段值
     */
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
     * 是否支持32位整数
     *
     * @return 结果
     */
    public boolean supportInt32() {
        return StringUtil.equalsIgnoreCase(this.getType(), "int");
    }

    /**
     * 是否支持64位整数
     *
     * @return 结果
     */
    public boolean supportInt64() {
        return StringUtil.equalsIgnoreCase(this.getType(), "long");
    }

    /**
     * 是否支持字符
     *
     * @return 结果
     */
    public boolean supportString() {
        return StringUtil.equalsIgnoreCase(this.getType(), "string");
    }

    /**
     * 是否支持日期
     *
     * @return 结果
     */
    public boolean supportDate() {
        return StringUtil.equalsIgnoreCase(this.getType(), "date");
    }

    /**
     * 是否支持布尔
     *
     * @return 结果
     */
    public boolean supportBoolean() {
        return StringUtil.equalsIgnoreCase(this.getType(), "boolean");
    }

    /**
     * 是否支持集合
     *
     * @return 结果
     */
    public boolean supportList() {
        return StringUtil.equalsIgnoreCase(this.getType(), "list");
    }

    /**
     * 是否支持对象
     *
     * @return 结果
     */
    public boolean supportObject() {
        return StringUtil.equalsIgnoreCase(this.getType(), "object");
    }

    /**
     * 是否支持二进制
     *
     * @return 结果
     */
    public boolean supportBinary() {
        return StringUtil.equalsIgnoreCase(this.getType(), "binary");
    }

    /**
     * 是否支持对象id
     *
     * @return 结果
     */
    public boolean supportObjectId() {
        return StringUtil.equalsIgnoreCase(this.getType(), "objectid");
    }

    /**
     * 是否支持代码
     *
     * @return 结果
     */
    public boolean supportCode() {
        return StringUtil.equalsIgnoreCase(this.getType(), "code");
    }

    /**
     * 设置字段名称
     *
     * @param name 字段名称
     */
    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
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

    /**
     * 字段是否无效（名称或类型为空）
     *
     * @return 是否无效
     */
    public boolean isInvalid() {
        return StringUtil.isBlank(this.getName()) || StringUtil.isBlank(this.getType());
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置数据库名称
     *
     * @param dbName 数据库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * 设置集合名称
     *
     * @param collectionName 集合名称
     */
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * 获取字段类型
     *
     * @return 字段类型
     */
    public String getType() {
        return typeProperty.get();
    }

    /**
     * 获取字段类型属性
     *
     * @return 字段类型属性
     */
    public StringProperty typeProperty() {
        return typeProperty;
    }

    /**
     * 获取字段值
     *
     * @return 字段值
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取字段名称
     *
     * @return 字段名称
     */
    public String getName() {
        return name;
    }

    /**
     * 是否为 _id 字段
     *
     * @return 是否为 _id 字段
     */
    public boolean is_id() {
        return MongoUtil.ID.equalsIgnoreCase(this.name);
    }

    /**
     * 获取字段别名
     *
     * @return 字段别名
     */
    public String getAliasName() {
        return aliasName;
    }

    /**
     * 设置字段别名
     *
     * @param aliasName 字段别名
     */
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    /**
     * 获取显示名称（优先使用别名）
     *
     * @return 显示名称
     */
    public String displayName() {
        return this.aliasName == null ? this.name : this.aliasName;
    }

    /**
     * 是否支持整数类型
     *
     * @return 是否支持整数类型
     */
    public boolean supportInteger() {
        return this.supportInt32() || this.supportInt64();
    }

    /**
     * 获取字段默认值
     *
     * @return 字段默认值
     */
    public Object defaultValue() {
        if (this.is_id() || this.supportObjectId()) {
            return null;
        }
        if (this.supportInt32()) {
            return 0;
        }
        if (this.supportInt64()) {
            return 0L;
        }
        if (this.supportDigits()) {
            return 0d;
        }
        if (this.supportObject()) {
            return "{}";
        }
        if (this.supportList()) {
            return "[]";
        }
        if (this.supportDate()) {
            return MongoUtil.DATE_FORMAT.format(new Date());
        }
        if (this.supportBinary()) {
            return new byte[]{};
        }
        if (this.supportBoolean()) {
            return false;
        }
        if (this.supportCode()) {
            return """
                    function func(){}
                    """;
        }
        return "";
    }
}
