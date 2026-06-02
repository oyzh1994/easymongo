package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.mongo.condition.MysqlCondition;
import cn.oyzh.easymongo.mongo.condition.MysqlConditionUtil;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class ShellMysqlConditionComboBox extends FXComboBox<MysqlCondition> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MysqlCondition o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        this.addItem(MysqlConditionUtil.conditions());
    }
}
