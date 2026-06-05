package cn.oyzh.easymongo.data;

import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.util.MongoDataUtil;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-09-04
 */
public abstract class MysqlTypeFileWriter implements Closeable {

    protected void init() throws Exception {

    }

    /**
     * 参数化
     *
     * @param column 字段
     * @param value  值
     * @param config 导出配置
     * @return 参数化后的值
     */
    public Object parameterized(MongoColumn column, Object value, MysqlDataExportConfig config) {
        if (value == null) {
            return "";
        }
        if (column.supportString()) {
            return MongoDataUtil.escapeQuotes((String) value);
        }
        if (column.supportInteger() || column.supportDigits()) {
            return value;
        }
        return value.toString();
    }

    /**
     * 写入头
     *
     * @throws Exception 异常
     */
    public void writeHeader() throws Exception {

    }

    /**
     * 写入尾
     *
     * @throws Exception 异常
     */
    public void writeTrial() throws Exception {

    }

    /**
     * 写入对象
     *
     * @param object 对象
     * @throws Exception 异常
     */
    public abstract void writeObject(Map<String, Object> object) throws Exception;

    /**
     * 写入多个对象
     *
     * @param objects 对象
     * @throws Exception 异常
     */
    public void writeObjects(List<Map<String, Object>> objects) throws Exception {
        for (Map<String, Object> object : objects) {
            this.writeObject(object);
        }
    }

    protected String formatLine(Object[] objects, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        return this.formatLine(List.of(objects), fieldSeparator, txtIdentifier, recordSeparator);
    }

    protected String formatLine(List<?> list, String fieldSeparator, String txtIdentifier, String recordSeparator) {
        StringBuilder sb = new StringBuilder();
        for (Object val : list) {
            sb.append(fieldSeparator)
                    .append(txtIdentifier)
                    .append(val)
                    .append(txtIdentifier);
        }
        sb.append(recordSeparator);
        return sb.substring(1);
    }

}
