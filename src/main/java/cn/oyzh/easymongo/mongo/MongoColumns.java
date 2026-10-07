package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库字段列表
 *
 * @author oyzh
 * @since 2024/07/10
 */
public class MongoColumns extends DBObjectList<MongoColumn> {

    /**
     * 构造数据库字段列表
     */
    public MongoColumns() {

    }

    /**
     * 使用字段集合构造数据库字段列表
     *
     * @param list 字段集合
     */
    public MongoColumns(List<MongoColumn> list) {
        super.addAll(list);
    }

    /**
     * 是否存在指定名称的字段
     *
     * @param name 字段名称
     * @return 是否存在
     */
    public boolean exists(String name) {
        return this.column(name) != null;
    }

    /**
     * 获取指定名称的字段
     *
     * @param name 字段名称
     * @return 字段
     */
    public MongoColumn column(String name) {
        if (!this.isEmpty()) {
            for (MongoColumn dbColumn : this) {
                if (StringUtil.equalsAnyIgnoreCase(dbColumn.getName(), name)) {
                    return dbColumn;
                }
            }
        }
        return null;
    }

    /**
     * 获取指定名称字段的索引
     *
     * @param name 字段名称
     * @return 索引
     */
    public int index(String name) {
        int index = 0;
        for (MongoColumn dbColumn : this) {
            if (dbColumn.getName().equals(name)) {
                break;
            }
            index++;
        }
        return index;
    }

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String collectionName() {
        for (MongoColumn dbColumn : this) {
            return dbColumn.getCollectionName();
        }
        return null;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        for (MongoColumn dbColumn : this) {
            return dbColumn.getDbName();
        }
        return null;
    }

    /**
     * 获取字段名称集合
     *
     * @return 字段名称集合
     */
    public List<String> columnNames() {
        List<String> list = new ArrayList<>();
        for (MongoColumn dbColumn : this) {
            list.add(dbColumn.getName());
        }
        return list;
    }
}
