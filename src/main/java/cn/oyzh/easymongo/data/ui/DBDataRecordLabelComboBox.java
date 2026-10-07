package cn.oyzh.easymongo.data.ui;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 数据记录标签下拉框
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class DBDataRecordLabelComboBox extends FXComboBox<String> {

    {
        this.addItem("(Root)");
        this.addItem("RECORDS");
    }

    /**
     * 是否根标签
     *
     * @return 是否根标签
     */
    public boolean isRoot() {
        return this.getSelectedIndex() == 0;
    }
}
