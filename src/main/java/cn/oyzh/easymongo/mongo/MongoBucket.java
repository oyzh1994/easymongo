package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;

/**
 * MongoDB 存储桶
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MongoBucket implements ObjectComparator<MongoBucket>, ObjectCopier<MongoBucket> {

    /**
     * 桶名称
     */
    private String name;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取桶名称
     *
     * @return 桶名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置桶名称
     *
     * @param name 桶名称
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
    public boolean compare(MongoBucket table) {
        if (table == null) {
            return false;
        }
        if (table == this) {
            return true;
        }
        if (!StringUtil.equals(this.getName(), table.getName())) {
            return false;
        }
        return StringUtil.equals(this.getDbName(), table.getDbName());
    }

    @Override
    public void copy(MongoBucket gridFS) {
        if (gridFS != null) {
            this.setName(gridFS.getName());
            this.setDbName(gridFS.getDbName());
        }
    }
}
