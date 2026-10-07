package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;

/**
 * 不为空条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MongoNotEmptyCondition extends MongoCondition {

    /**
     * 不为空条件实例
     */
    public final static MongoNotEmptyCondition INSTANCE = new MongoNotEmptyCondition();

    /**
     * 构造不为空条件
     */
    public MongoNotEmptyCondition() {
        super(I18nHelper.notIsEmpty(), "!=''", false);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        Bson bson1;
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = Filters.expr(
                    new Document("$ne", Arrays.asList(
                            new Document("$toString", "$_id"),
                            ""
                    ))
            );
        } else {
            bson1 = Filters.ne(columnName,"");
        }
        return bson1;
    }

}
