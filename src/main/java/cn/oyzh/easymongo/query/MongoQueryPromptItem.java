package cn.oyzh.easymongo.query;


/**
 * 查询提示内容
 *
 * @author oyzh
 * @since 2024/02/21
 */
public class MongoQueryPromptItem {

    /**
     * 类型
     * 1 collection
     * 2 function
     * 4 keyword
     */
    private byte type;

    /**
     * 内容
     */
    private String content;

    /**
     * 相关度
     */
    private double correlation;

    /**
     * 额外内容
     */
    private String extContent;

    /**
     * 是否函数类型
     *
     * @return 是否函数类型
     */
    public boolean isFunctionType() {
        return 2 == this.type;
    }

    /**
     * 是否集合类型
     *
     * @return 是否集合类型
     */
    public boolean isCollectionType() {
        return 1 == this.type;
    }

    /**
     * 是否关键字类型
     *
     * @return 是否关键字类型
     */
    public boolean isKeywordType() {
        return 4 == this.type;
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    public byte getType() {
        return type;
    }

    /**
     * 设置类型
     *
     * @param type 类型
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取相关度
     *
     * @return 相关度
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * 设置相关度
     *
     * @param correlation 相关度
     */
    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    /**
     * 获取额外内容
     *
     * @return 额外内容
     */
    public String getExtContent() {
        return extContent;
    }

    /**
     * 设置额外内容
     *
     * @param extContent 额外内容
     */
    public void setExtContent(String extContent) {
        this.extContent = extContent;
    }
}
