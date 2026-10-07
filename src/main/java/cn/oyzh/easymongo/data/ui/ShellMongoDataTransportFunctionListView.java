package cn.oyzh.easymongo.data.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.dto.ShellMongoDataTransportFunction;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输函数列表视图，以复选框列表展示并选择需要传输的函数
 *
 * @author oyzh
 * @since 2024/09/05
 */
public class ShellMongoDataTransportFunctionListView extends FXListView<FXCheckBox> {

    /**
     * 选中项变更回调
     */
    private Runnable selectedChanged;

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

    /**
     * 根据 Mongo 函数列表构建传输函数并初始化视图
     *
     * @param functions 函数列表
     */
    public void of(List<MongoFunction> functions) {
        List<ShellMongoDataTransportFunction> list = CollectionUtil.newArrayList();
        for (MongoFunction function : functions) {
            ShellMongoDataTransportFunction obj = new ShellMongoDataTransportFunction();
            obj.setName(function.getName());
            list.add(obj);
        }
        this.init(list);
    }

    /**
     * 根据传输函数列表初始化视图
     *
     * @param functions 传输函数列表
     */
    public void init(List<ShellMongoDataTransportFunction> functions) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(functions)) {
            for (ShellMongoDataTransportFunction function : functions) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setText(function.getName());
                checkBox.setSelected(function.isSelected());
                checkBox.setProp("data", function);
                checkBox.selectedChanged((observable, oldValue, newValue) -> {
                    function.setSelected(newValue);
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
     * 获取已选中的传输函数列表
     *
     * @return 已选中的传输函数列表
     */
    public List<ShellMongoDataTransportFunction> getSelectedFunctions() {
        List<ShellMongoDataTransportFunction> list = new ArrayList<>();
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
}
