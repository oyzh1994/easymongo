package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;

/**
 * 在列表条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class MysqlInListCondition extends MysqlCondition {

    public final static MysqlInListCondition INSTANCE = new MysqlInListCondition();

    public MysqlInListCondition() {
        super(I18nHelper.inList(), "IN");
    }

    public MysqlInListCondition(String name, String value) {
        super(name, value);
    }

}
