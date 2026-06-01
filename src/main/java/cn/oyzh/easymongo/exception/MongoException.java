package cn.oyzh.easymongo.exception;

/**
 * redis异常
 *
 * @author oyzh
 * @since 2023/12/10
 */
public class MongoException extends RuntimeException {

    public MongoException() {
        super();
    }

    public MongoException(String message) {
        super(message);
    }

    public MongoException(Exception ex) {
        super(ex);
    }
}
