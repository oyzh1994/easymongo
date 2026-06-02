package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

/**
 * 等于条件
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEqCondition extends MysqlCondition {

    public final static MysqlEqCondition INSTANCE = new MysqlEqCondition();

    public MysqlEqCondition() {
        super(I18nHelper.eq(), "=");
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        Bson bson1;
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = Filters.expr(
                    new Document("$eq", Arrays.asList(
                            new Document("$toString", "$_id"),
                            condition
                    ))
            );
        } else {
            bson1 = Filters.and(Filters.exists(columnName), Filters.eq(columnName, condition));
        }
        return bson1;
    }
}
