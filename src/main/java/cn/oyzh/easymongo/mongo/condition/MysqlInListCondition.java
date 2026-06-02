package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        List<?> list = null;
        if (condition instanceof String str) {
            String[] arr = str.split(",");
            list = Arrays.asList(arr);
        } else if (condition instanceof List<?> l) {
            list = l;
        }
        if (list == null) {
            return null;
        }
        Bson bson1;
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = Filters.expr(
                    new Document("$in", Arrays.asList(
                            new Document("$toString", "$_id"),
                            list
                    ))
            );
        } else {
            bson1 = Filters.and(Filters.exists(columnName), Filters.in(columnName, list));
        }
        return bson1;
    }
}
