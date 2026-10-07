package cn.oyzh.easymongo.query;


import cn.oyzh.common.util.StringUtil;

/**
 * 查询词元
 *
 * @author oyzh
 * @since 2024/8/15
 */
public class MongoQueryToken {

    /**
     * 结束位置
     */
    private int endIndex;

    /**
     * 开始位置
     */
    private int startIndex;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 空格
     * 2 .
     * 3 "
     */
    private Character token;

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return StringUtil.isEmpty(this.content);
    }

    /**
     * 是否非空
     *
     * @return 是否非空
     */
    public boolean isNotEmpty() {
        return StringUtil.isNotEmpty(this.content);
    }

    /**
     * 是否为可能的关键字
     *
     * @return 是否为可能的关键字
     */
    public boolean isPossibilityKeyword() {
        return ' ' == this.token || '\n' == this.token;
    }

    /**
     * 是否为可能的函数
     *
     * @return 是否为可能的函数
     */
    public boolean isPossibilityFunction() {
        return '.' == this.token;
    }

    /**
     * 是否为可能的集合
     *
     * @return 是否为可能的集合
     */
    public boolean isPossibilityCollection() {
        return '"' == this.token;
    }

    /**
     * 获取结束位置
     *
     * @return 结束位置
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * 设置结束位置
     *
     * @param endIndex 结束位置
     */
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * 获取开始位置
     *
     * @return 开始位置
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 设置开始位置
     *
     * @param startIndex 开始位置
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
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
     * 获取词元字符
     *
     * @return 词元字符
     */
    public Character getToken() {
        return token;
    }

    /**
     * 设置词元字符
     *
     * @param token 词元字符
     */
    public void setToken(Character token) {
        this.token = token;
    }
}
