package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.BooleanUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据库对象状态，记录对象的新增、变更、删除等状态信息
 *
 * @author oyzh
 * @since 2024/07/13
 */
public class DBObjectStatus {

    /**
     * 对象是否变更
     */
    private SimpleBooleanProperty changedProperty;

    /**
     * 对象是否删除
     */
    private SimpleBooleanProperty deletedProperty;

    /**
     * 对象是否新增
     */
    private SimpleBooleanProperty createdProperty;

    /**
     * 变更数据
     */
    private Map<String, Boolean> changedFlag;

    /**
     * 原始数据
     */
    private Map<String, Object> originalData;

    /**
     * 获取是否变更属性
     *
     * @return 是否变更属性
     */
    public SimpleBooleanProperty changedProperty() {
        if (this.changedProperty == null) {
            this.changedProperty = new SimpleBooleanProperty();
        }
        return this.changedProperty;
    }

    /**
     * 获取是否删除属性
     *
     * @return 是否删除属性
     */
    public SimpleBooleanProperty deletedProperty() {
        if (this.deletedProperty == null) {
            this.deletedProperty = new SimpleBooleanProperty();
        }
        return this.deletedProperty;
    }

    /**
     * 获取是否新增属性
     *
     * @return 是否新增属性
     */
    public SimpleBooleanProperty createdProperty() {
        if (this.createdProperty == null) {
            this.createdProperty = new SimpleBooleanProperty();
        }
        return this.createdProperty;
    }

    /**
     * 获取变更标记
     *
     * @return 变更标记
     */
    private Map<String, Boolean> changedFlag() {
        if (this.changedFlag == null) {
            this.changedFlag = new HashMap<>();
        }
        return this.changedFlag;
    }

    /**
     * 设置变更标记
     *
     * @param key   标记键
     * @param value 标记值
     */
    private void setChangedFlag(String key, Boolean value) {
        // 已变更
        if (BooleanUtil.isTrue(value)) {
            this.changedFlag().put(key, value);
        } else {// 未变更
            this.changedFlag().remove(key);
        }
        this.setChanged(!this.changedFlag().isEmpty());
    }

    /**
     * 清除变更标记
     */
    protected void clearChangedFlag() {
        if (this.changedFlag != null) {
            this.changedFlag().clear();
        }
    }

    /**
     * 获取原始数据
     *
     * @return 原始数据
     */
    protected Map<String, Object> originalData() {
        if (this.originalData == null) {
            this.originalData = new HashMap<>();
        }
        return this.originalData;
    }

    /**
     * 记录原始数据并同步变更标记
     *
     * @param key   数据键
     * @param value 数据值
     */
    protected void putOriginalData(String key, Object value) {
        JulLog.info("putOriginalData: key={}, value={}", key, value);
        if (this.originalData().containsKey(key)) {
            Object val = this.getOriginalData(key);
            this.setChangedFlag(key, !Objects.equals(val, value));
        } else {
            this.originalData().put(key, value);
        }
    }

    /**
     * 获取指定键的原始数据
     *
     * @param key 数据键
     * @return 原始数据
     */
    protected Object getOriginalData(String key) {
        if (this.originalData == null) {
            return null;
        }
        return this.originalData().get(key);
    }

    /**
     * 清除原始数据
     */
    public void clearOriginalData() {
        if (this.originalData != null) {
            this.originalData().clear();
        }
    }

    /**
     * 校验指定键的原始数据是否发生变化
     *
     * @param key         数据键
     * @param currentData 当前数据
     * @return 是否发生变化
     */
    protected Boolean checkOriginalData(String key, Object currentData) {
        return !Objects.equals(this.getOriginalData(key), currentData);
    }

    /**
     * 初始化状态
     */
    public void initStatus() {

    }

    /**
     * 设置是否变更
     *
     * @param changed 是否变更
     */
    public void setChanged(boolean changed) {
        this.changedProperty().set(changed);
        this.updateStatus();
    }

    /**
     * 是否已变更
     *
     * @return 是否已变更
     */
    public boolean isChanged() {
        return this.changedProperty != null && this.changedProperty.get();
    }

    /**
     * 设置是否删除
     *
     * @param deleted 是否删除
     */
    public void setDeleted(boolean deleted) {
        this.deletedProperty().set(deleted);
        this.updateStatus();
    }

    /**
     * 是否已删除
     *
     * @return 是否已删除
     */
    public boolean isDeleted() {
        return this.deletedProperty != null && this.deletedProperty.get();
    }

    /**
     * 设置是否新增
     *
     * @param created 是否新增
     */
    public void setCreated(boolean created) {
        this.createdProperty().set(created);
        this.updateStatus();
    }

    /**
     * 是否已新增
     *
     * @return 是否已新增
     */
    public boolean isCreated() {
        return this.createdProperty != null && this.createdProperty.get();
    }

    /**
     * 清除状态
     */
    public void clearStatus() {
        this.setChanged(false);
        this.setCreated(false);
        this.setDeleted(false);
        this.updateStatus();
        this.clearChangedFlag();
    }

    /**
     * 更新状态文本
     */
    public void updateStatus() {
        if (this.isCreated()) {
            this.statusProperty().set("+");
        } else if (this.isChanged()) {
            this.statusProperty().set("*");
        } else {
            this.statusProperty().set("");
        }
    }

    /**
     * 状态属性
     */
    private SimpleStringProperty statusProperty;

    /**
     * 获取状态属性
     *
     * @return 状态属性
     */
    public SimpleStringProperty statusProperty() {
        if (this.statusProperty == null) {
            this.statusProperty = new SimpleStringProperty();
        }
        return statusProperty;
    }

    // @Override
    // public boolean equals(Object o) {
    //     return o == this;
    // }

    /**
     * 获取状态文本
     *
     * @return 状态文本
     */
    public String getStatus() {
        return this.statusProperty().get();
    }
}
