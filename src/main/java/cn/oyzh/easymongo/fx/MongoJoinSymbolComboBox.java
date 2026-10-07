package cn.oyzh.easymongo.fx;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;

/**
 * MongoDB条件连接符号选择框
 *
 * @author oyzh
 * @since 2024/1/26
 */
public class MongoJoinSymbolComboBox extends FXComboBox<String> {

    @Override
    public void initNode(){
        this.addItem("AND");
        this.addItem("OR");
    }
}
