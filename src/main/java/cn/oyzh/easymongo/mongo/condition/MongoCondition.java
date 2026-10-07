package cn.oyzh.easymongo.mongo.condition;


import org.bson.conversions.Bson;

/**
 * MongoDB 查询条件
 *
 * @author oyzh
 * @since 2024/06/26
 */
public abstract class MongoCondition {

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 需要条件标志位
     */
    private boolean requireCondition = true;

    /**
     * 构造查询条件
     */
    public MongoCondition() {

    }

    /**
     * 使用名称、值构造查询条件
     *
     * @param name  名称
     * @param value 值
     */
    public MongoCondition(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 使用名称、值、是否需要条件构造查询条件
     *
     * @param name             名称
     * @param value            值
     * @param requireCondition 是否需要条件
     */
    public MongoCondition(String name, String value, boolean requireCondition) {
        this.name = name;
        this.value = value;
        this.requireCondition = requireCondition;
    }

    /**
     * 构建条件
     *
     * @param columnName 列名称
     * @return 条件
     */
    public Bson wrapCondition(String columnName) {
        return this.wrapCondition(columnName, null);
    }

    /**
     * 构建条件
     *
     * @param columnName 列名称
     * @param condition  条件值
     * @return 条件
     */
    public Bson wrapCondition(String columnName, Object condition) {
        return null;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 是否需要条件
     *
     * @return 是否需要条件
     */
    public boolean isRequireCondition() {
        return requireCondition;
    }

    /**
     * 设置是否需要条件
     *
     * @param requireCondition 是否需要条件
     */
    public void setRequireCondition(boolean requireCondition) {
        this.requireCondition = requireCondition;
    }
}
