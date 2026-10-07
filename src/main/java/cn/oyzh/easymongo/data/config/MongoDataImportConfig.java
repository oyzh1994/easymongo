package cn.oyzh.easymongo.data.config;


import cn.oyzh.common.util.StringUtil;

import java.nio.charset.StandardCharsets;

/**
 * MongoDB 数据导入配置
 *
 * @author oyzh
 * @since 2024/09/02
 */
public class MongoDataImportConfig {

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 导入模式
     * 1. 追加
     * 2. 复制
     */
    private String importMode = "2";

    /**
     * 字段索引
     */
    private int columnIndex = 0;

    /**
     * 数据起始索引
     */
    private int dataStartIndex = 1;

    /**
     * 字段标签
     */
    private String recordLabel;

    /**
     * 属性作为字段
     */
    private boolean attrToColumn;

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
     * 是否为追加模式
     *
     * @return 是否为追加模式
     */
    public boolean isAppendMode() {
        return StringUtil.equals(this.importMode, "1");
    }

    /**
     * 是否为复制模式
     *
     * @return 是否为复制模式
     */
    public boolean isCopyMode() {
        return StringUtil.equals(this.importMode, "2");
    }

    /**
     * 获取字段分割字符
     *
     * @return 字段分割字符
     */
    public char fieldSeparatorChar() {
        return this.fieldSeparator.charAt(0);
    }

    /**
     * 获取文本识别字符
     *
     * @return 文本识别字符
     */
    public char txtIdentifierChar() {
        return this.txtIdentifier.charAt(0);
    }

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
     * 获取导入模式
     *
     * @return 导入模式
     */
    public String getImportMode() {
        return importMode;
    }

    /**
     * 设置导入模式
     *
     * @param importMode 导入模式
     */
    public void setImportMode(String importMode) {
        this.importMode = importMode;
    }

    /**
     * 获取字段索引
     *
     * @return 字段索引
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * 设置字段索引
     *
     * @param columnIndex 字段索引
     */
    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * 获取数据起始索引
     *
     * @return 数据起始索引
     */
    public int getDataStartIndex() {
        return dataStartIndex;
    }

    /**
     * 设置数据起始索引
     *
     * @param dataStartIndex 数据起始索引
     */
    public void setDataStartIndex(int dataStartIndex) {
        this.dataStartIndex = dataStartIndex;
    }

    /**
     * 获取字段标签
     *
     * @return 字段标签
     */
    public String getRecordLabel() {
        return recordLabel;
    }

    /**
     * 设置字段标签
     *
     * @param recordLabel 字段标签
     */
    public void setRecordLabel(String recordLabel) {
        this.recordLabel = recordLabel;
    }

    /**
     * 是否将属性作为字段
     *
     * @return 是否将属性作为字段
     */
    public boolean isAttrToColumn() {
        return attrToColumn;
    }

    /**
     * 设置是否将属性作为字段
     *
     * @param attrToColumn 是否将属性作为字段
     */
    public void setAttrToColumn(boolean attrToColumn) {
        this.attrToColumn = attrToColumn;
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
}
