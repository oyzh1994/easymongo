package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.i18n.I18nHelper;
import org.bson.conversions.Bson;

/**
 * 介于条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class MysqlBetweenCondition extends MysqlCondition {

    public final static MysqlBetweenCondition INSTANCE = new MysqlBetweenCondition();

    public MysqlBetweenCondition() {
        super(I18nHelper.between(), "BETWEEN");
    }

    public MysqlBetweenCondition(String name, String value) {
        super(name, value);
    }

}
