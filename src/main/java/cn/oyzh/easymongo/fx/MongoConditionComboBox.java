package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.mongo.condition.MongoCondition;
import cn.oyzh.easymongo.mongo.condition.MongoConditionUtil;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * @author oyzh
 * @since 2024/06/26
 */
public class MongoConditionComboBox extends FXComboBox<MongoCondition> {

    {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MongoCondition o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        this.addItem(MongoConditionUtil.conditions());
    }
}
