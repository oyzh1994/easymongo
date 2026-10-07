package cn.oyzh.easymongo.data.ui;

import cn.oyzh.common.system.OSUtil;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 数据记录分隔符下拉框
 *
 * @author oyzh
 * @since 2024/09/04
 */
public class DBDataRecordSeparatorComboBox extends FXComboBox<String> {

    {
        this.addItem("CRLF");
        this.addItem("LF");
        this.addItem("CR");
        if (OSUtil.isWindows()) {
            this.select(0);
        } else if (OSUtil.isLinux()) {
            this.select(1);
        } else if (OSUtil.isMacOS()) {
            this.select(1);
        }
    }

    /**
     * 获取记录分隔符
     *
     * @return 记录分隔符
     */
    public String value() {
        int itemIndex = this.getSelectedIndex();
        if (itemIndex == 0) {
            return "\r\n";
        }
        if (itemIndex == 1) {
            return "\n";
        }
        return "\r";
    }
}
