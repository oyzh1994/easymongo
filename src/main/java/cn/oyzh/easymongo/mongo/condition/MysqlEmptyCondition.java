package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.Collections;

/**
 * 包含条件
 *
 * @author oyzh
 * @since 2024/6/27
 */
public class MysqlEmptyCondition extends MysqlCondition {

    public final static MysqlEmptyCondition INSTANCE = new MysqlEmptyCondition();

    public MysqlEmptyCondition() {
        super(I18nHelper.isEmpty(), "=''", false);
    }

    @Override
    public Bson wrapCondition(String columnName, Object condition) {
        return Filters.eq(columnName, "");
    }
}
