package cn.oyzh.easymongo.dto;


/**
 * MongoDB shell 连接信息
 *
 * @author oyzh
 * @since 2023/9/20
 */
public class ShellMongoConnectInfo {

    /**
     * 原始输入内容
     */
    private String input;

    /**
     * 地址
     */
    private String host = "localhost";

    /**
     * 端口
     */
    private int port = 2181;

    /**
     * 超时时间，单位毫秒
     */
    private int timeout = 5000;

    /**
     * 只读模式
     */
    private boolean readonly;

    /**
     * 获取原始输入内容
     *
     * @return 原始输入内容
     */
    public String getInput() {
        return input;
    }

    /**
     * 设置原始输入内容
     *
     * @param input 原始输入内容
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * 获取地址
     *
     * @return 地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置地址
     *
     * @param host 地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取端口
     *
     * @return 端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取超时时间
     *
     * @return 超时时间，单位毫秒
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间，单位毫秒
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 是否只读模式
     *
     * @return 是否只读
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * 设置只读模式
     *
     * @param readonly 是否只读
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
