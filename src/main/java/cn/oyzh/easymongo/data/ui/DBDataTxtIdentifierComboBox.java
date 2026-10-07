package cn.oyzh.easymongo.data.ui;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * 数据文本标识符下拉框
 *
 * @author oyzh
 * @since 2024/09/04
 */
public class DBDataTxtIdentifierComboBox extends FXComboBox<String> {

    {
        this.addItem("\"");
        this.addItem("'");
    }
}
