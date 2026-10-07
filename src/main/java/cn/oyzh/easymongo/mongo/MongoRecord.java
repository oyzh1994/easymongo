package cn.oyzh.easymongo.mongo;


import cn.oyzh.common.object.Destroyable;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.util.MongoUtil;
import org.bson.BsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 数据库记录
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class MongoRecord extends DBObjectStatus implements Destroyable, ObjectCopier<MongoRecord> {

    /**
     * 是否只读
     */
    private final boolean readonly;

    /**
     * 是否可编辑
     */
    private boolean editable;

    /**
     * 字段列表
     */
    private MongoColumns columns;

    /**
     * 使用字段列表构造数据库记录（非只读）
     *
     * @param columns 字段列表
     */
    public MongoRecord(MongoColumns columns) {
        this(columns, false);
    }

    /**
     * 使用字段集合构造数据库记录（非只读）
     *
     * @param columns 字段集合
     */
    public MongoRecord(List<MongoColumn> columns) {
        this(new MongoColumns(columns), false);
    }

    /**
     * 使用字段集合、是否只读构造数据库记录
     *
     * @param columns  字段集合
     * @param readonly 是否只读
     */
    public MongoRecord(List<MongoColumn> columns, boolean readonly) {
        this(new MongoColumns(columns), readonly);
    }

    /**
     * 使用字段列表、是否只读构造数据库记录
     *
     * @param columns  字段列表
     * @param readonly 是否只读
     */
    public MongoRecord(MongoColumns columns, boolean readonly) {
        this.columns = columns;
        this.readonly = readonly;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public MongoColumns getColumns() {
        return columns;
    }

    /**
     * 数据
     */
    private HashMap<String, MongoRecordProperty> properties = new HashMap<>();

    /**
     * 添加数据
     *
     * @param column 字段名
     * @param value  值
     * @return 数据属性
     */
    public MongoRecordProperty putValue(String column, Object value) {
        MongoRecordProperty property = this.getProperty(column);
        if (property == null) {
            property = putValue(new MongoColumn(column), value);
        } else {
            property.set(value);
        }
        return property;
    }

    /**
     * 添加数据
     *
     * @param column 字段
     * @param value  值
     * @return 数据属性
     */
    public MongoRecordProperty putValue(MongoColumn column, Object value) {
        MongoRecordProperty property = this.getProperty(column.getName());
        if (property == null) {
            property = new MongoRecordProperty(this, column, value, this.readonly);
            property.changedProperty().addListener((observable, oldValue, newValue) -> this.updateStatus());
            this.properties.put(column.getName(), property);
        } else {
            property.setValue(value);
        }
        return property;
    }

    /**
     * 获取数据
     *
     * @param column 字段名
     * @return 数据
     */
    public Object getValue(String column) {
        MongoRecordProperty property = this.getProperty(column);
        return property == null ? null : property.get();
    }

    /**
     * 获取原始数据
     *
     * @param column 字段名
     * @return 原始数据
     */
    public Object getOriginal(String column) {
        MongoRecordProperty property = this.getProperty(column);
        return property == null ? null : property.getOriginal();
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public Set<String> columns() {
        return this.properties.keySet();
    }

    /**
     * 获取记录属性
     *
     * @param key 键
     * @return 属性
     */
    public MongoRecordProperty getProperty(String key) {
        return this.properties.get(key);
    }

    /**
     * 是否存在记录属性
     *
     * @param recordProperty 记录属性
     * @return 属性
     */
    public boolean hasProperty(MongoRecordProperty recordProperty) {
        return this.properties.containsValue(recordProperty);
    }

    /**
     * 移除记录属性
     *
     * @param key 键
     * @return 数据属性
     */
    public MongoRecordProperty removeProperty(String key) {
        return this.properties.remove(key);
    }

    /**
     * 清除数据
     */
    public void clearProperty() {
        this.properties.clear();
    }

    /**
     * 更新数据
     *
     * @param rowData 新数据
     */
    public void update(Map<String, Object> rowData) {
        if (rowData != null) {
            for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                this.putValue(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public boolean isChanged() {
        if (super.isChanged()) {
            return true;
        }
        for (MongoRecordProperty property : this.properties.values()) {
            if (property.isChanged()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearStatus() {
        for (MongoRecordProperty property : this.properties.values()) {
            property.setChanged(false);
            property.updateOriginal();
        }
        super.clearStatus();
    }

    /**
     * 抛弃变更
     *
     * @throws Exception 异常
     */
    public void discard() throws Exception {
        for (MongoRecordProperty property : this.properties.values()) {
            property.discard();
        }
        super.clearStatus();
    }

    @Override
    public void copy(MongoRecord record) {
        if (record == null) {
            return;
        }
        List<MongoColumn> delList = new ArrayList<>();
        if (record.columns != null) {
            List<MongoColumn> addList = new ArrayList<>();
            for (MongoColumn column : record.columns) {
                if (column.is_id()) {
                    continue;
                }
                MongoColumn mongoColumn = this.column(column.getName());
                if (mongoColumn == null) {
                    addList.add(column);
                } else {
                    mongoColumn.copy(column);
                }
            }
            for (MongoColumn column : this.columns) {
                if (column.is_id()) {
                    continue;
                }
                Optional<MongoColumn> optional = record.columns.stream()
                        .filter(f -> StringUtil.equals(column.getName(), f.getName()))
                        .findAny();
                if (optional.isEmpty()) {
                    delList.add(column);
                }
            }
            this.columns.addAll(addList);
            this.columns.removeAll(delList);
        }
        if (record.properties != null) {
            for (MongoColumn column : record.columns) {
                if (column.is_id()) {
                    continue;
                }
                Object value = record.getValue(column.getName());
                this.putValue(column, value);
            }
        }
        if (!delList.isEmpty()) {
            this.setChanged(true);
            for (MongoColumn column : delList) {
                MongoRecordProperty property = this.getProperty(column.getName());
                if (property != null) {
                    property.set(null);
                    this.removeProperty(column.getName());
                }
            }
        }
    }

    /**
     * 纠正字段
     *
     * @param columns 字段列表
     */
    public void correctColumns(MongoColumns columns) {
        if (columns != null) {
            List<MongoColumn> addList = new ArrayList<>();
            for (MongoColumn column : columns) {
                if (column.is_id()) {
                    continue;
                }
                MongoColumn mongoColumn = this.column(column.getName());
                if (mongoColumn == null) {
                    addList.add(column);
                }
            }
            this.columns.addAll(addList);
            for (MongoColumn column : addList) {
                this.putValue(column, null);
            }
        }
    }

    //public MongoRecordData getRecordData() {
    //    MongoRecordData recordData = new MongoRecordData();
    //    for (String column : this.columns()) {
    //        MongoRecordProperty property = this.getProperty(column);
    //        if (property != null) {
    //            Object value = property.get();
    //            if (value != null) {
    //                recordData.put(property.getColumn(), value);
    //            }
    //        }
    //    }
    //    return recordData;
    //}
    //
    //public MongoRecordData getChangedRecordData() {
    //    MongoRecordData recordData = new MongoRecordData();
    //    for (String column : this.columns()) {
    //        MongoRecordProperty property = this.getProperty(column);
    //        if (property != null && property.isChanged()) {
    //            recordData.put(property.getColumn(), property.get());
    //        }
    //    }
    //    return recordData;
    //}
    //
    //public MongoRecordData getOriginalRecordData() {
    //    MongoRecordData recordData = new MongoRecordData();
    //    for (String column : this.columns()) {
    //        MongoRecordProperty property = this.getProperty(column);
    //        if (property != null) {
    //            Object val = property.getOriginal();
    //            recordData.put(property.getColumn(), val);
    //        }
    //    }
    //    return recordData;
    //}

    /**
     * 指定字段是否已变更
     *
     * @param column 字段名称
     * @return 是否已变更
     */
    public boolean isColumnChanged(String column) {
        return false;
    }

    /**
     * 转换为 Map
     *
     * @return Map 数据
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, MongoRecordProperty> value : this.properties.entrySet()) {
            map.put(value.getKey(), value.getValue().get());
        }
        return map;
    }

    @Override
    public void destroy() {
        if (this.properties != null) {
            this.columns = null;
            for (MongoRecordProperty property : this.properties.values()) {
                property.destroy();
            }
            this.properties.clear();
            this.properties = null;
        }
    }

    /**
     * 是否可编辑
     *
     * @return 是否可编辑
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * 设置是否可编辑
     *
     * @param editable 是否可编辑
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * 设置 _id 值
     *
     * @param _id _id 值
     */
    public void set_id(BsonValue _id) {
        this.putValue(MongoUtil.ID, _id);
    }

    /**
     * 获取指定名称的字段
     *
     * @param columnName 字段名称
     * @return 字段
     */
    public MongoColumn column(String columnName) {
        if (this.columns == null) {
            return null;
        }
        return this.columns.column(columnName);
    }

    /**
     * 获取 _id 字段
     *
     * @return _id 字段
     */
    public MongoColumn _idColumn() {
        return this.column(MongoUtil.ID);
    }

    /**
     * 获取 _id 值
     *
     * @return _id 值
     */
    public Object _idValue() {
        MongoRecordProperty property = this.getProperty(MongoUtil.ID);
        if (property == null) {
            return null;
        }
        return property.getOriginal();
    }
}
