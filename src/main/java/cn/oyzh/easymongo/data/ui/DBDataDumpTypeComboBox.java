package cn.oyzh.easymongo.data.ui;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.i18n.I18nHelper;

/**
 * 数据转储类型下拉框
 *
 * @author oyzh
 * @since 2024/08/22
 */
public class DBDataDumpTypeComboBox extends FXComboBox<String> {

    {
        this.addItem(I18nHelper.dataAndStructure());
        this.addItem(I18nHelper.structure());
    }

    /**
     * 是否「数据和结构」
     *
     * @return 是否「数据和结构」
     */
    public boolean isFull() {
        return this.getSelectedIndex() == 0;
    }

}
