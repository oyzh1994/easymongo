package cn.oyzh.easymongo.util;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.UUIDUtil;
import cn.oyzh.easymongo.exception.MongoException;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoRecordData;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * db工具类
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoUtil {

    public static final String ID = "_id";

    /**
     * 打印数据
     *
     * @param data 数据
     */
    public static void printData(MongoRecordData data) {
        if (data != null) {
            for (Map.Entry<MongoColumn, Object> entry : data.entries()) {
                JulLog.info(entry.getKey().getName() + "=" + entry.getValue());
            }
            JulLog.info("printData======================>");
        }
    }

    public static String wrap(String name) {
        StringBuilder builder = new StringBuilder(name);
        return builder.toString();
    }

    public static Object wrapData(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Number) {
            return val;
        }
        if (val instanceof CharSequence v) {
            String v1 = v.toString();
            if (v1.isEmpty()) {
                return "''";
            }
            if (!v1.startsWith("'") && !v1.startsWith("\"")) {
                v1 = "'" + v1;
            }
            if (!v1.endsWith("'") && !v1.endsWith("\"")) {
                v1 = v1 + "'";
            }
            return v1;
        }
        if (val instanceof LocalDateTime) {
            return "'" + val + "'";
        }
        return val;
    }

    public static Object unwrapData(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof CharSequence v) {
            String v1 = v.toString();
            if (v1.isEmpty()) {
                return null;
            }
            if (v1.startsWith("'") || v1.startsWith("\"")) {
                v1 = v1.substring(1);
            }
            if (v1.endsWith("'") || v1.endsWith("\"")) {
                v1 = v1.substring(0, v1.length() - 1);
            }
            return v1;
        }
        return val;
    }

    public static void setVal(PreparedStatement statement, Object val, int index) throws SQLException {
        if (val == null) {
            statement.setNull(index, JDBCType.NULL.ordinal());
        } else if (val instanceof Byte x) {
            statement.setByte(index, x);
        } else if (val instanceof Short x) {
            statement.setShort(index, x);
        } else if (val instanceof Integer x) {
            statement.setInt(index, x);
        } else if (val instanceof Long x) {
            statement.setLong(index, x);
        } else if (val instanceof Float x) {
            statement.setFloat(index, x);
        } else if (val instanceof Double x) {
            statement.setDouble(index, x);
        } else if (val instanceof CharSequence x) {
            statement.setString(index, x.toString());
        } else if (val instanceof Date x) {
            statement.setDate(index, x);
        } else if (val instanceof Timestamp x) {
            statement.setTimestamp(index, x);
        } else if (val instanceof java.util.Date x) {
            statement.setDate(index, new Date(x.getTime()));
        } else if (val instanceof LocalDate x) {
            statement.setDate(index, Date.valueOf(x));
        } else if (val instanceof LocalDateTime x) {
            statement.setTimestamp(index, Timestamp.valueOf(x));
        } else if (val instanceof Object x) {
            statement.setObject(index, x);
        }
    }

    public static boolean isSameVal(Object val, Object nVal) {
        if (val == nVal) {
            return true;
        }
        if (Objects.equals(val, nVal)) {
            return true;
        }
        if (val instanceof Number n1 && nVal instanceof Number n2) {
            if (n1.doubleValue() == n2.doubleValue()) {
                return true;
            }
        }
        if (val instanceof byte[] b1 && nVal instanceof byte[] b2) {
            if (StringUtil.equals(new String(b1), new String(b2))) {
                return true;
            }
        }
        return false;
    }

    public static void rollback(Connection connection) {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (SQLException ex) {
            throw new MongoException(ex);
        }
    }

    public static int executeUpdate(PreparedStatement statement) throws SQLException {
        int result = statement.executeUpdate();
        statement.close();
        return result;
    }

    public static void close(AutoCloseable o) throws Exception {
        if (o instanceof ResultSet resultSet) {
            resultSet.close();
        } else if (o instanceof Statement statement) {
            statement.close();
        } else if (o instanceof Connection connection) {
            connection.close();
        } else if (o != null) {
            o.close();
        }
    }


    public static String wrap(String s, String tableName) {
        return "";
    }

    /**
     * 获取值类型
     *
     * @param val 值
     * @return 类型
     */
    public static String getType(Object val) {
//        if (val instanceof BigDecimal decimal) {
//            return decimal.stripTrailingZeros().scale() <= 0 ? "int" : "double";
//        }
        if (val instanceof Double || val instanceof Float) {
            return "double";
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte) {
            return "int";
        }
        if (val instanceof Number) {
            return "double";
        }
        if (val instanceof Character || val instanceof CharSequence) {
            return "string";
        }
        return "string";
    }
}
