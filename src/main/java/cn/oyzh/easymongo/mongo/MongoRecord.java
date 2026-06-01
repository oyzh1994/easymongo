package cn.oyzh.easymongo.mongo;


import cn.oyzh.common.object.Destroyable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * db记录
 *
 * @author oyzh
 * @since 2023/12/20
 */
public class MongoRecord extends cn.oyzh.easymongo.mongo.DBObjectStatus implements Destroyable {

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

    public MongoRecord(MongoColumns columns) {
        this(columns, false);
    }

    public MongoRecord(List<MongoColumn> columns) {
        this(new MongoColumns(columns), false);
    }

    public MongoRecord(List<MongoColumn> columns, boolean readonly) {
        this(new MongoColumns(columns), readonly);
    }

    public MongoRecord(MongoColumns columns, boolean readonly) {
        this.columns = columns;
        this.readonly = readonly;
    }

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
            property.setValue(value);
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
     * 清除数据
     */
    public void clear() {
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
     */
    public void discard() throws Exception {
        for (MongoRecordProperty property : this.properties.values()) {
            property.discard();
        }
        super.clearStatus();
    }

    public void copy(MongoRecord record) {
        if (record != null) {
            for (String column : record.columns()) {
                Object value = record.getValue(column);
                if (value != null) {
                    this.putValue(column, value);
                }
            }
        }
    }

    public MongoRecordData getRecordData() {
        MongoRecordData recordData = new MongoRecordData();
        for (String column : this.columns()) {
            MongoRecordProperty property = this.getProperty(column);
            if (property != null) {
                Object value = property.get();
                if (value != null) {
                    recordData.put(property.getColumn(), value);
                }
            }
        }
        return recordData;
    }

    public MongoRecordData getChangedRecordData() {
        MongoRecordData recordData = new MongoRecordData();
        for (String column : this.columns()) {
            MongoRecordProperty property = this.getProperty(column);
            if (property != null && property.isChanged()) {
                recordData.put(property.getColumn(), property.get());
            }
        }
        return recordData;
    }

    public MongoRecordData getOriginalRecordData() {
        MongoRecordData recordData = new MongoRecordData();
        for (String column : this.columns()) {
            MongoRecordProperty property = this.getProperty(column);
            if (property != null) {
                Object val = property.getOriginal();
                // if (val != null) {
                recordData.put(property.getColumn(), val);
                // }
            }
        }
        return recordData;
    }

    public boolean isColumnChanged(String column) {
        return false;
    }

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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
