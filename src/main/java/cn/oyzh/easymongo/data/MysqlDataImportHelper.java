package cn.oyzh.easymongo.data;

import cn.oyzh.common.date.DateUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoDataUtil;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/02
 */
public class MysqlDataImportHelper {

    /**
     * 参数化
     *
     * @param column 字段
     * @param value  值
     * @return 参数化后的值
     */
    public static Object parameterized(MongoColumn column, Object value, MysqlDataImportConfig config) throws ParseException {
        if (value == null) {
            return null;
        }
        if (value.toString().isEmpty()) {
            return null;
        }
        if (column.supportString()) {
            return MongoDataUtil.escapeQuotes(value.toString());
        }
        return value;
    }

    /**
     * 转换为插入sql
     *
     * @param columns 字段列表
     * @param records 记录
     * @param config  配置
     * @return 插入sql
     */
    public static List<String> toInsertSql(MongoColumns columns, List<MongoRecord> records, MysqlDataImportConfig config) throws Exception {
        List<String> insertSql = new ArrayList<>();
        return insertSql;
    }
}
