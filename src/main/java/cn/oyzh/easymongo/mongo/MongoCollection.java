package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;

import java.util.Comparator;

/**
 * MongoDB 集合
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MongoCollection implements ObjectComparator<MongoCollection>, ObjectCopier<MongoCollection> {

    /**
     * 集合名称
     */
    private String name;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置集合名称
     *
     * @param name 集合名称
     */
    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean compare(MongoCollection collection) {
        if (collection == null) {
            return false;
        }
        if (collection == this) {
            return true;
        }
        if (!StringUtil.equals(this.getName(), collection.getName())) {
            return false;
        }
        return StringUtil.equals(this.getDbName(), collection.getDbName());
    }

    @Override
    public void copy(MongoCollection collection) {
        if (collection != null) {
            this.setName(collection.getName());
            this.setDbName(collection.getDbName());
        }
    }
}
