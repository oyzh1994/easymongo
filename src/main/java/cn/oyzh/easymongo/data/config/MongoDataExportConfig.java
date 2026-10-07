package cn.oyzh.easymongo.data.config;


import java.nio.charset.StandardCharsets;

/**
 * MongoDB 数据导出配置
 *
 * @author oyzh
 * @since 2024/09/02
 */
public class MongoDataExportConfig {

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 字段作为属性
     */
    private boolean fieldToAttr;

    /**
     * 包含列标题
     */
    private boolean includeFields = true;

    /**
     * 记录分割符号
     */
    private String recordSeparator = System.lineSeparator();

    /**
     * 字段分割符号
     */
    private String fieldSeparator = ";";

    /**
     * 文本识别符号
     */
    private String txtIdentifier = "\"";

    /**
     * 字符集
     */
    private String charset = StandardCharsets.UTF_8.displayName();

    /**
     * 早期版本
     */
    private boolean earlyVersion;

    /**
     * 获取日期格式
     *
     * @return 日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * 设置日期格式
     *
     * @param dateFormat 日期格式
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * 是否将字段作为属性
     *
     * @return 是否将字段作为属性
     */
    public boolean isFieldToAttr() {
        return fieldToAttr;
    }

    /**
     * 设置是否将字段作为属性
     *
     * @param fieldToAttr 是否将字段作为属性
     */
    public void setFieldToAttr(boolean fieldToAttr) {
        this.fieldToAttr = fieldToAttr;
    }

    /**
     * 是否包含列标题
     *
     * @return 是否包含列标题
     */
    public boolean isIncludeFields() {
        return includeFields;
    }

    /**
     * 设置是否包含列标题
     *
     * @param includeFields 是否包含列标题
     */
    public void setIncludeFields(boolean includeFields) {
        this.includeFields = includeFields;
    }

    /**
     * 获取记录分割符号
     *
     * @return 记录分割符号
     */
    public String getRecordSeparator() {
        return recordSeparator;
    }

    /**
     * 设置记录分割符号
     *
     * @param recordSeparator 记录分割符号
     */
    public void setRecordSeparator(String recordSeparator) {
        this.recordSeparator = recordSeparator;
    }

    /**
     * 获取字段分割符号
     *
     * @return 字段分割符号
     */
    public String getFieldSeparator() {
        return fieldSeparator;
    }

    /**
     * 设置字段分割符号
     *
     * @param fieldSeparator 字段分割符号
     */
    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * 获取文本识别符号
     *
     * @return 文本识别符号
     */
    public String getTxtIdentifier() {
        return txtIdentifier;
    }

    /**
     * 设置文本识别符号
     *
     * @param txtIdentifier 文本识别符号
     */
    public void setTxtIdentifier(String txtIdentifier) {
        this.txtIdentifier = txtIdentifier;
    }

    /**
     * 获取字符集
     *
     * @return 字符集
     */
    public String getCharset() {
        return charset;
    }

    /**
     * 设置字符集
     *
     * @param charset 字符集
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 是否为早期版本
     *
     * @return 是否为早期版本
     */
    public boolean isEarlyVersion() {
        return earlyVersion;
    }

    /**
     * 设置是否为早期版本
     *
     * @param earlyVersion 是否为早期版本
     */
    public void setEarlyVersion(boolean earlyVersion) {
        this.earlyVersion = earlyVersion;
    }
}
