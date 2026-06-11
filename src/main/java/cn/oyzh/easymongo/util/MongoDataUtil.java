package cn.oyzh.easymongo.util;

import cn.oyzh.common.util.Base64Util;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordProperty;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * 获取记录脚本
     *
     * @param record
     * @return 脚本
     */
    public static String getRecordScript(MongoRecord record) {
        StringBuilder builder = new StringBuilder();
        for (MongoColumn column : record.getColumns()) {
            String colName = column.getName();
            MongoRecordProperty property = record.getProperty(colName);
            if (property == null) {
                continue;
            }
            Object value = property.get();
            if (value == null) {
                continue;
            }
            buildRecordData(colName, value, builder, 1);
        }
        return "{" + builder.substring(1) + "\n}";
    }

    /**
     * 构建记录值
     *
     * @param value 值
     * @param deep  当前深度
     * @return 结果
     */
    private static Object buildRecordValue(Object value, int deep) {
        String type = MongoUtil.getType(value);

        if ("int".equals(type)) {
            return "Int32(" + value + ")";
        }

        if ("double".equals(type) || "boolean".equals(type)) {
            return value;
        }

        if ("obejectid".equals(type)) {
            ObjectId id = (ObjectId) value;
            return "ObjectId(\"" + id.toHexString() + "\")";
        }

        if ("date".equals(type)) {
            Date date = (Date) value;
            return "ISODate(\"" + MongoUtil.DATE_FORMAT.format(date) + "\")";
        }

        if ("binary".equals(type)) {
            byte[] bytes;
            if (value instanceof byte[] bytes1) {
                bytes = bytes1;
            } else if (value instanceof Binary binary) {
                bytes = binary.getData();
            } else if (value instanceof BsonBinary binary) {
                bytes = binary.getData();
            } else {
                bytes = new byte[]{};
            }
            return "Binary.createFromBase64(\"" + Base64Util.encodeToString(bytes) + "\", 0)";
        }

        if ("list".equals(type)) {
            List<?> list = (List<?>) value;
            StringBuilder sb = new StringBuilder();
            for (Object val1 : list) {
                Object val = buildRecordValue(val1, deep + 1);
                sb.append(",\n").repeat("\t", deep + 1).append(val);
            }
            return "[" + sb.substring(1) + "\n" + "\t".repeat(deep) + "]";
        }

        if ("object".equals(type)) {
            Document document = (Document) value;
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                buildRecordData(entry.getKey(), entry.getValue(), sb, deep + 1);
            }
            return "{" + sb.substring(1) + "\n\t".repeat(deep) + "}";
        }

        return "\"" + value + "\"";
    }

    /**
     * 构建记录数据
     *
     * @param colName 字段名
     * @param value   值
     * @param builder 缓存
     * @param deep    深度
     */
    private static void buildRecordData(String colName, Object value, StringBuilder builder, int deep) {
        builder.append(",\n");
        builder.repeat("\t", deep);
        builder.append(colName)
                .append(": ")
                .append(buildRecordValue(value, deep));
    }

    /**
     * 转换为插入脚本
     *
     * @param record 记录
     * @return 结果
     */
    public static String toInsertScript(MongoRecord record) {
        MongoColumn column = record._idColumn();
        String sql = """
                db.getCollection("$collection").insert($doc);
                """;
        String script = getRecordScript(record);
        return sql.replace("$collection", column.getCollectionName()).replace("$doc", script);
    }

    /**
     * 转换为更新脚本
     *
     * @param record 记录
     * @return 结果
     */
    public static String toUpdateScript(MongoRecord record) {
        MongoColumn column = record._idColumn();
        Object id = record._idValue();
        String sql = """
                db.getCollection("$collection").update({_id: $id},{$set: $doc});
                """;
        String script = getRecordScript(record);
        return sql.replace("$collection", column.getCollectionName()).replace("$id", id.toString()).replace("$doc", script);
    }

    /**
     * 转换为插入脚本
     *
     * @param records 记录列表
     * @return 结果
     */
    public static List<String> toInsertScript(List<MongoRecord> records) {
        List<String> list = new ArrayList<>();
        for (MongoRecord record : records) {
            list.add(toInsertScript(record));
        }
        return list;
    }

    /**
     * 转换为替换脚本
     *
     * @param function 记录
     * @return 结果
     */
    public static String toReplaceScript(MongoFunction function) {
        String script = """
                db.getCollection("$collectionName").replaceOne(
                    { _id: "$name" },
                    { _id: "$name", value: Code("$code") },
                    { upsert: true }
                );
                """;
        script = script.replace("$collectionName", MongoUtil.SYSTEM_JS);
        script = script.replace("$name", function.getName());
        script = script.replace("$name", function.getName());
        script = script.replace("$code", function.getCode());
        return script;
    }


}
