package cn.oyzh.easymongo.data.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.dto.ShellMongoDataTransportCollection;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输集合列表视图，以复选框列表展示并选择需要传输的集合
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class ShellMongoDataTransportTableListView extends FXListView<FXCheckBox> {

    /**
     * 选中项变更回调
     */
    private Runnable selectedChanged;

    /**
     * 根据 Mongo 集合列表构建传输集合并初始化视图
     *
     * @param tables 集合列表
     */
    public void of(List<MongoCollection> tables) {
        List<ShellMongoDataTransportCollection> list = CollectionUtil.newArrayList();
        for (MongoCollection table : tables) {
            ShellMongoDataTransportCollection obj = new ShellMongoDataTransportCollection();
            obj.setName(table.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 根据传输集合列表初始化视图
     *
     * @param tables 传输集合列表
     */
    public void init(List<ShellMongoDataTransportCollection> tables) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(tables)) {
            for (ShellMongoDataTransportCollection table : tables) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(table.getName());
                checkBox.setSelected(table.isSelected());
                checkBox.setProp("data", table);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    table.setSelected(newValue);
                    if (this.selectedChanged != null) {
                        this.selectedChanged.run();
                    }
                });
                ListViewUtil.selectRowOnMouseClicked(checkBox);
                this.addItem(checkBox);
            }
        }
        if (this.selectedChanged != null) {
            this.selectedChanged.run();
        }
    }

    /**
     * 获取已选中的集合列表
     *
     * @return 已选中的集合列表
     */
    public List<ShellMongoDataTransportCollection> getSelectedTables() {
        List<ShellMongoDataTransportCollection> list = new ArrayList<>();
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                list.add(item.getProp("data"));
            }
        }
        return list;
    }

    /**
     * 获取已选中项的数量
     *
     * @return 已选中项的数量
     */
    public int getSelectedSize() {
        int size = 0;
        for (FXCheckBox item : this.getItems()) {
            if (item.isSelected()) {
                size++;
            }
        }
        return size;
    }

    /**
     * 获取选中项变更回调
     *
     * @return 选中项变更回调
     */
    public Runnable getSelectedChanged() {
        return selectedChanged;
    }

    /**
     * 设置选中项变更回调
     *
     * @param selectedChanged 选中项变更回调
     */
    public void setSelectedChanged(Runnable selectedChanged) {
        this.selectedChanged = selectedChanged;
    }
}
