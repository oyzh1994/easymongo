package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.condition.MongoCondition;
import cn.oyzh.easymongo.store.MongoConnectStore;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

import java.util.List;

/**
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoConnectComboBox extends FXComboBox<MongoConnect> {

    @Override
    public void initNode() {
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(MongoConnect o) {
                if (o == null) {
                    return "";
                }
                return o.getName();
            }
        });
        List<MongoConnect> connects = MongoConnectStore.INSTANCE.selectList();
        this.setItem(connects);
    }

}
