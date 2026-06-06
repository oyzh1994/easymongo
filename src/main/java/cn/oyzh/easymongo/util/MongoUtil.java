package cn.oyzh.easymongo.util;

import cn.oyzh.common.util.StringUtil;
import org.bson.BsonValue;
import org.bson.Document;
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
        if (val instanceof Document) {
            return "object";
        }
        if (val instanceof BsonValue bsonValue) {
            if (bsonValue.isInt32() || bsonValue.isInt64()) {
                return "int";
            }
            if (bsonValue.isDouble() || bsonValue.isDecimal128()) {
                return "double";
            }
            if (bsonValue.isDouble() || bsonValue.isDecimal128() || bsonValue.isNumber()) {
                return "double";
            }
            if (bsonValue.isDateTime() || bsonValue.isTimestamp()) {
                return "date";
            }
            if (bsonValue.isArray()) {
                return "list";
            }
            if (bsonValue.isBinary()) {
                return "binary";
            }
            if (bsonValue.isBoolean()) {
                return "boolean";
            }
            if (bsonValue.isString()) {
                return "string";
            }
            if (bsonValue.isDocument()) {
                return "object";
            }
            if (bsonValue.isObjectId()) {
                return "obejectid";
            }
        }
        return "object";
    }

    /**
     * 是否原始类型
     *
     * @param val 值
     * @return 类型
     */
    public static boolean isPrimaryType(Object val) {
        return StringUtil.equalsAnyIgnoreCase(getType(val), "int", "double", "list", "boolean");
    }

    /**
     * 是否json类型
     *
     * @param val 值
     * @return 类型
     */
    public static boolean isJsonType(Object val) {
        return StringUtil.equalsIgnoreCase(getType(val), "list");

    }
}
