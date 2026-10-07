package cn.oyzh.easymongo.query;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果集
 *
 * @author oyzh
 * @since 2024/02/19
 */
public class MongoQueryResults<R extends MongoQueryResult> {

    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 结果列表
     */
    private List<R> results;

    /**
     * 添加结果
     *
     * @param result 结果
     */
    public void addResult(R result) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(result);
    }

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(this.results);
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return StringUtil.isEmpty(this.errMsg);
    }

    /**
     * 解析异常信息
     *
     * @param ex 异常
     */
    public void parseError(Exception ex) {
        this.errMsg = ex.getMessage();
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * 设置错误消息
     *
     * @param errMsg 错误消息
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    /**
     * 获取结果列表
     *
     * @return 结果列表
     */
    public List<R> getResults() {
        return results;
    }

    /**
     * 设置结果列表
     *
     * @param results 结果列表
     */
    public void setResults(List<R> results) {
        this.results = results;
    }
}
