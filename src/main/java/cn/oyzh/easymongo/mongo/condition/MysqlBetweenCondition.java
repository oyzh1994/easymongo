package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;

import java.util.Collection;

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

    @Override
    public String wrapCondition(Object condition) {
        if (condition instanceof Object[] arr) {
            return this.getValue() + " " + MongoUtil.wrapData(arr[0]) + " AND " + MongoUtil.wrapData(arr[1]);
        }
        if (condition instanceof Collection<?> coll) {
            return this.getValue() + " " + MongoUtil.wrapData(CollectionUtil.get(coll, 0)) + " AND " + MongoUtil.wrapData(CollectionUtil.get(coll, 1));
        }
        return super.wrapCondition(condition);
    }
}
