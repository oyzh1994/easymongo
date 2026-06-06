package cn.oyzh.easymongo.util;

import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;

/**
 * @author oyzh
 * @since 2024/08/26
 */
public class MongoDataUtil {

    /**
     * 转义符号
     *
     * @param str 内容
     * @return 转义后的内容
     */
    public static String escapeQuotes(String str) {
        if (str != null && (str.contains("'") ||
                str.contains("\"") ||
                str.contains("\\") ||
                str.contains("\r") ||
                str.contains("\n"))) {
            StringBuilder sb = new StringBuilder();
            for (char c : str.toCharArray()) {
                if (c == '\'') {
                    //                    sb.append("\\'");
                    sb.append(c);
                } else if (c == '"') {
                    sb.append("\\\"");
                } else if (c == '\\') {
                    sb.append("\\\\");
                } else if (c == '\r') {
                    sb.append("\\r");
                } else if (c == '\n') {
                    sb.append("\\n");
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return str;
    }

    public static String toInsertSql(MongoColumns columns, MongoRecord record, boolean b) {
        return null;
    }

    public static String toUpdateSql(MongoColumns columns, MongoRecord record) {
        return null;
    }
}
