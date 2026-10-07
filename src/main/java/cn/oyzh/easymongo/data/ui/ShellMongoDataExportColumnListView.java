package cn.oyzh.easymongo.data.ui;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.data.dto.ShellMongoDataExportColumn;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.list.FXListView;
import cn.oyzh.fx.plus.util.ListViewUtil;

import java.util.List;

/**
 * 数据导出列列表视图，以复选框列表展示并选择需要导出的列
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class ShellMongoDataExportColumnListView extends FXListView<FXCheckBox> {

    /**
     * 根据导出列初始化列表视图
     *
     * @param columns 导出列列表
     */
    public void init(List<ShellMongoDataExportColumn> columns) {
        this.clearItems();
        if (CollectionUtil.isNotEmpty(columns)) {
            for (ShellMongoDataExportColumn column : columns) {
                FXCheckBox checkBox = new FXCheckBox();
                checkBox.setSelected(column.isSelected());
                checkBox.setText(column.getName());
                checkBox.selectedChanged((observable, oldValue, newValue) -> column.setSelected(newValue));
                ListViewUtil.selectRowOnMouseClicked(checkBox);
                this.addItem(checkBox);
            }
        }
    }
}
