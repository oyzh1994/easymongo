package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

/**
 * 为空条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MongoEmptyCondition extends MongoCondition {

    /**
     * 为空条件实例
     */
    public final static MongoEmptyCondition INSTANCE = new MongoEmptyCondition();

    /**
     * 构造为空条件
     */
    public MongoEmptyCondition() {
        super(I18nHelper.isEmpty(), "=''", false);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        return Filters.eq(columnName, "");
    }
}
