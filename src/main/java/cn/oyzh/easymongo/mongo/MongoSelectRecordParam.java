package cn.oyzh.easymongo.mongo;


import java.util.List;

/**
 * 查询记录参数
 *
 * @author oyzh
 * @since 2024-09-13
 */
public class MongoSelectRecordParam {

    /**
     * 起始位置
     */
    private Long start;

    /**
     * 查询数量
     */
    private Long limit;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 集合名称
     */
    private String collectionName;

    /**
     * 是否只读
     */
    private boolean readonly;

    /**
     * 列集合
     */
    private List<MongoColumn> columns;

    /**
     * 过滤条件集合
     */
    private List<MongoRecordFilter> filters;

    /**
     * 构造查询记录参数
     */
    public MongoSelectRecordParam() {
    }

    /**
     * 使用数据库名称、集合名称构造查询记录参数
     *
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     */
    public MongoSelectRecordParam(String dbName, String collectionName) {
        this.dbName = dbName;
        this.collectionName = collectionName;
    }

    /**
     * 是否包含分页控制
     *
     * @return 是否包含分页控制
     */
    public boolean hasPageControl() {
        return this.start != null && this.limit != null;
    }

    /**
     * 获取起始位置
     *
     * @return 起始位置
     */
    public Long getStart() {
        return start;
    }

    /**
     * 设置起始位置
     *
     * @param start 起始位置
     */
    public void setStart(Long start) {
        this.start = start;
    }

    /**
     * 获取查询数量
     *
     * @return 查询数量
     */
    public Long getLimit() {
        return limit;
    }

    /**
     * 设置查询数量
     *
     * @param limit 查询数量
     */
    public void setLimit(Long limit) {
        this.limit = limit;
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
     * 获取是否只读
     *
     * @return 是否只读
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * 设置是否只读
     *
     * @param readonly 是否只读
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 获取列集合
     *
     * @return 列集合
     */
    public List<MongoColumn> getColumns() {
        return columns;
    }

    /**
     * 设置列集合
     *
     * @param columns 列集合
     */
    public void setColumns(List<MongoColumn> columns) {
        this.columns = columns;
    }

    /**
     * 获取过滤条件集合
     *
     * @return 过滤条件集合
     */
    public List<MongoRecordFilter> getFilters() {
        return filters;
    }

    /**
     * 设置过滤条件集合
     *
     * @param filters 过滤条件集合
     */
    public void setFilters(List<MongoRecordFilter> filters) {
        this.filters = filters;
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

}
