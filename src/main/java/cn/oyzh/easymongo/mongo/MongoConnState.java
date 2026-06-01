package cn.oyzh.easymongo.mongo;


/**
 * shell连接状态
 *
 * @author oyzh
 * @since 2023/07/1
 */
public enum MongoConnState {

    /**
     * 未初始化
     */
    NOT_INITIALIZED {
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 已连接
     */
    CONNECTED {
        public boolean isConnected() {
            return true;
        }
    },
    /**
     * 连接中
     */
    CONNECTING {
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 已关闭
     */
    CLOSED {
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 失败
     */
    FAILED {
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 中断
     */
    INTERRUPTED {
        public boolean isConnected() {
            return false;
        }
    },
    /**
     * 重连
     */
    RECONNECTED {
        public boolean isConnected() {
            return true;
        }
    };

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public abstract boolean isConnected();
}
