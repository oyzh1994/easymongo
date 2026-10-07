package cn.oyzh.easymongo.data.ui;

import cn.oyzh.easymongo.data.dto.ShellMongoDataExportCollection;
import cn.oyzh.fx.plus.controls.table.FXTableView;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据导出集合表格视图，以表格形式展示并选择需要导出的集合
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class ShellMongoDataExportCollectionTableView extends FXTableView<ShellMongoDataExportCollection> {

    /**
     * 获取已选中的集合列表
     *
     * @return 已选中的集合列表
     */
    public List<ShellMongoDataExportCollection> getSelectedTables() {
        List<ShellMongoDataExportCollection> exportTables = new ArrayList<>();
        for (ShellMongoDataExportCollection item : this.getItems()) {
            if (item.isSelected()) {
                exportTables.add(item);
            }
        }
        return exportTables;
    }

    /**
     * 是否存在已选中的集合
     *
     * @return 是否存在已选中的集合
     */
    public boolean hasSelectedTable() {
        for (ShellMongoDataExportCollection item : this.getItems()) {
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }
}
