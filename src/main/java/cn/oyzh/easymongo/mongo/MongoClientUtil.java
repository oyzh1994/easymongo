package cn.oyzh.easymongo.mongo;

import cn.oyzh.easymongo.domain.MongoConnect;

/**
 * MongoDB 客户端工具类
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoClientUtil {

    /**
     * 创建客户端
     *
     * @param connect 连接信息
     * @return 客户端
     */
    public static MongoClient newClient(MongoConnect connect) {
        return new MongoClient(connect);
    }
}
