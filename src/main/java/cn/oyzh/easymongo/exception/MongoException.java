package cn.oyzh.easymongo.exception;

/**
 * MongoDB 异常
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class MongoException extends RuntimeException {

    /**
     * 构造异常
     */
    public MongoException() {
        super();
    }

    /**
     * 构造异常
     *
     * @param message 异常信息
     */
    public MongoException(String message) {
        super(message);
    }

    /**
     * 构造异常
     *
     * @param ex 异常原因
     */
    public MongoException(Exception ex) {
        super(ex);
    }
}
