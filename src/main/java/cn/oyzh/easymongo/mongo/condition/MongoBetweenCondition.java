package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

/**
 * 介于条件
 *
 * @author oyzh
 * @since 2024/6/28
 */
public class MongoBetweenCondition extends MongoCondition {

    /**
     * 介于条件实例
     */
    public final static MongoBetweenCondition INSTANCE = new MongoBetweenCondition();

    /**
     * 构造介于条件
     */
    public MongoBetweenCondition() {
        super(I18nHelper.between(), "BETWEEN");
    }

    /**
     * 使用名称、值构造介于条件
     *
     * @param name  名称
     * @param value 值
     */
    public MongoBetweenCondition(String name, String value) {
        super(name, value);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        Bson bson1;
        List<?> list = (List<?>) condition;
        Object f = list.getFirst();
        Object l = list.getLast();
        if (MongoUtil.ID.equals(columnName)) {
            bson1 = Filters.expr(
                    new Document("$and", Arrays.asList(
                            new Document("$gte", Arrays.asList(new Document("$toString", "$_id"), f)),
                            new Document("$lte", Arrays.asList(new Document("$toString", "$_id"), l))
                    ))

            );
        } else {
            bson1 = Filters.and(Filters.exists(columnName), Filters.gte(columnName, f), Filters.lte(columnName, l));
        }
        return bson1;
    }

}
