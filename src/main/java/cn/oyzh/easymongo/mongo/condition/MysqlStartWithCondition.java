package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.regex.Pattern;

/**
 * 开始以条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlStartWithCondition extends MysqlCondition {

    public final static MysqlStartWithCondition INSTANCE = new MysqlStartWithCondition();

    public MysqlStartWithCondition() {
        super(I18nHelper.startWith(), "LIKE");
    }

    public MysqlStartWithCondition(String name, String value) {
        super(name, value);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        String quote = Pattern.quote(condition.toString());
        Pattern pattern = Pattern.compile("^" + quote, Pattern.CASE_INSENSITIVE);
        Bson bson1;
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = MysqlConditionUtil.idFilter(pattern);
        } else {
            bson1 = Filters.and(Filters.exists(columnName), Filters.regex(columnName, pattern));
        }
        return bson1;
    }
}
