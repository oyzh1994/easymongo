package cn.oyzh.easymongo.util;

import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * db工具类
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoUtil {

    public static final String ID = "_id";

    /**
     * 获取值类型
     *
     * @param val 值
     * @return 类型
     */
    public static String getType(Object val) {
        if (val instanceof Long
                || val instanceof Integer
                || val instanceof Short
                || val instanceof Byte) {
            return "int";
        }
        if (val instanceof Number) {
            return "double";
        }
        if (val instanceof Character || val instanceof CharSequence) {
            return "string";
        }
        if (val instanceof List<?>) {
            return "list";
        }
        if (val instanceof Boolean) {
            return "boolean";
        }
        if (val instanceof java.util.Date) {
            return "date";
        }
        if (val instanceof Binary) {
            return "binary";
        }
        if (val instanceof ObjectId) {
            return "obejectid";
        }
        return "string";
    }
}
