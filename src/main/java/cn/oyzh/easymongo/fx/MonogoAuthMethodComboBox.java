package cn.oyzh.easymongo.fx;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB认证方式选择框
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MonogoAuthMethodComboBox extends FXComboBox<String> {

    /**
     * 获取当前认证方式
     *
     * @return 认证方式，none、password或unknown
     */
    public String getType() {
        if (this.getSelectedIndex() == 0) {
            return "none";
        }
        if (this.getSelectedIndex() == 1) {
            return "password";
        }
        return "unknown";
    }

    @Override
    public void select(String obj) {
        if ("none".equalsIgnoreCase(obj)) {
            this.select(0);
        } else if ("password".equalsIgnoreCase(obj)) {
            this.select(1);
        }
    }

    @Override
    public void initNode() {
        this.addItem(I18nHelper.none());
        this.addItem(I18nHelper.password());
        super.initNode();
    }
}
