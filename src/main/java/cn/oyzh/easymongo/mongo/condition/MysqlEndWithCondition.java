package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.regex.Pattern;

/**
 * 结束以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEndWithCondition extends MysqlCondition {

    public final static MysqlEndWithCondition INSTANCE = new MysqlEndWithCondition();

    public MysqlEndWithCondition() {
        super(I18nHelper.endWith(), "LIKE");
    }

    public MysqlEndWithCondition(String name, String value) {
        super(name, value);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        String quote = Pattern.quote(condition.toString());
        Pattern pattern = Pattern.compile(quote + "$", Pattern.CASE_INSENSITIVE);
        Bson bson1;
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = MysqlConditionUtil.idFilterRegex(pattern);
        } else {
            bson1 = Filters.and(Filters.exists(columnName), Filters.regex(columnName, pattern));
        }
        return bson1;
    }
}
