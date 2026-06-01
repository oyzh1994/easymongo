package cn.oyzh.easymongo.util;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;

import java.util.ArrayList;
import java.util.HashMap;
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
     * 参数化，json
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForJson(MongoColumn column, Object value) {
        if (value == null) {
            return null;
        }
        if (column.supportString()) {
            return escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，xml
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForXml(MongoColumn column, Object value) {
        if (value == null) {
            return null;
        }
        if (column.supportString()) {
            return escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，csv
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForCsv(MongoColumn column, Object value) {
        if (value == null) {
            return "";
        }
        if (column.supportString()) {
            return "\"" + escapeQuotes((String) value) + "\"";
        }
        return "\"" + value + "\"";
    }

    /**
     * 参数化，sql
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForSql(MongoColumn column, Object value) {
        if (value == null) {
            return "NULL";
        }
        if (column.supportString()) {
            String str = escapeQuotes((String) value);
            return "'" + str + "'";
        }
        return value;
    }

    /**
     * 参数化，html
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForHtml(MongoColumn column, Object value) {
        if (value == null) {
            return "";
        }
        if (column.supportString()) {
            return escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 参数化，xls
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterizedForXls(MongoColumn column, Object value) {
        if (value == null) {
            return null;
        }
        if (column.supportString()) {
            return escapeQuotes((String) value);
        }
        return value;
    }

    /**
     * 转换为插入sql
     *
     * @param columns       字段列表
     * @param record        记录
     * @param includeFields 包含字段
     * @return 插入sql
     */
    public static String toInsertSql(MongoColumns columns, MongoRecord record, boolean includeFields) {
        List<String> list = toInsertSql(columns, List.of(record), includeFields);
        return CollectionUtil.getFirst(list);
    }

    /**
     * 转换为插入sql
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入sql
     */
    public static List<String> toInsertSql(MongoColumns columns, List<MongoRecord> records) {
        return toInsertSql(columns, records, false);
    }

    /**
     * 转换为插入sql
     *
     * @param columns       字段列表
     * @param records       记录
     * @param includeFields 包含字段
     * @return 插入sql
     */
    public static List<String> toInsertSql(MongoColumns columns, List<MongoRecord> records, boolean includeFields) {
        List<String> list = new ArrayList<>();
        String tableName = columns.tableName();
        final String sqlBase = "INSERT INTO " + MongoUtil.wrap(tableName);
        for (MongoRecord record : records) {
            StringBuilder sql = new StringBuilder(sqlBase);
            if (includeFields) {
                sql.append("(");
                for (MongoColumn dbColumn : columns) {
                    sql.append(MongoUtil.wrap(dbColumn.getName())).append(", ");
                }
                if (sql.toString().endsWith(", ")) {
                    sql.delete(sql.length() - 2, sql.length());
                }
                sql.append(")");
            }
            sql.append(" VALUES (");
            for (MongoColumn dbColumn : columns) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForSql(dbColumn, value);
                sql.append(value).append(", ");
            }
            if (sql.toString().endsWith(", ")) {
                sql.delete(sql.length() - 2, sql.length());
            }
            sql.append(");");
            list.add(sql.toString());
        }
        return list;
    }

    /**
     * 转换为修改sql
     *
     * @param columns 字段列表
     * @param record  记录
     * @return 修改sql
     */
    public static String toUpdateSql(MongoColumns columns, MongoRecord record) {
        String tableName = columns.tableName();
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ")
                .append(MongoUtil.wrap(columns.dbName(), tableName))
                .append(" SET ");
        for (MongoColumn column : columns) {
            Object value = record.getValue(column.getName());
            value = parameterizedForSql(column, value);
            builder.append(MongoUtil.wrap(column.getName()));
            builder.append(" = ");
            builder.append(value);
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append(" WHERE ");
        builder.append(";");
        return builder.toString();
    }

    /**
     * 转换为插入json
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入json
     */
    public static List<Map<String, Object>> toInsertJson(MongoColumns columns, List<MongoRecord> records) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<MongoColumn> columnList = columns;
        for (MongoRecord record : records) {
            Map<String, Object> object = new HashMap<>();
            for (MongoColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForJson(dbColumn, value);
                object.put(dbColumn.getName(), value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入xml
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入xml
     */
    public static List<Map<String, Object>> toInsertXml(MongoColumns columns, List<MongoRecord> records) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<MongoColumn> columnList = columns;
        for (MongoRecord record : records) {
            Map<String, Object> object = new HashMap<>();
            for (MongoColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForXml(dbColumn, value);
                object.put(dbColumn.getName(), value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入csv
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入csv
     */
    public static List<List<Object>> toInsertCsv(MongoColumns columns, List<MongoRecord> records) {
        List<List<Object>> list = new ArrayList<>();
        List<MongoColumn> columnList = columns;
        for (MongoRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (MongoColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForCsv(dbColumn, value);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入html
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入html
     */
    public static List<List<Object>> toInsertHtml(MongoColumns columns, List<MongoRecord> records) {
        List<List<Object>> list = new ArrayList<>();
        List<MongoColumn> columnList = columns;
        for (MongoRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (MongoColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForHtml(dbColumn, value);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }

    /**
     * 转换为插入xls
     *
     * @param columns 字段列表
     * @param records 记录
     * @return 插入xls
     */
    public static List<List<Object>> toInsertXls(MongoColumns columns, List<MongoRecord> records) {
        List<List<Object>> list = new ArrayList<>();
        List<MongoColumn> columnList = columns;
        for (MongoRecord record : records) {
            List<Object> object = new ArrayList<>();
            for (MongoColumn dbColumn : columnList) {
                Object value = record.getValue(dbColumn.getName());
                value = parameterizedForXls(dbColumn, value);
                object.add(value);
            }
            list.add(object);
        }
        return list;
    }
}
