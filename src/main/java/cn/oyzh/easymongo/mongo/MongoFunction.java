package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;

/**
 * MongoDB 函数
 *
 * @author oyzh
 * @since 2026-06-11
 */
public class MongoFunction implements ObjectCopier<MongoFunction>, ObjectComparator<MongoFunction> {

    /**
     * 函数名称
     */
    private String name;

    /**
     * 函数代码
     */
    private String code;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置函数名称
     *
     * @param name 函数名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取函数代码
     *
     * @return 函数代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置函数代码
     *
     * @param code 函数代码
     */
    public void setCode(String code) {
        this.code = code;
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
     * 是否为新函数（函数代码为空）
     *
     * @return 是否为新函数
     */
    public boolean isNew() {
        return StringUtil.isBlank(this.getCode());
    }

    @Override
    public void copy(MongoFunction function) {
        if (function != null) {
            this.setName(function.getName());
            this.setCode(function.getCode());
            this.setDbName(function.getDbName());
        }
    }

    @Override
    public boolean compare(MongoFunction t1) {
        if (t1 == null) {
            return false;
        }
        if (t1 == this) {
            return true;
        }
        if (!StringUtil.equals(this.getName(), t1.getName())) {
            return false;
        }
        return StringUtil.equals(this.getDbName(), t1.getDbName());
    }
}
