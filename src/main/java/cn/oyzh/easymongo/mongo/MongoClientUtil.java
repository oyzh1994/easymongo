package cn.oyzh.easymongo.mongo;

import cn.oyzh.easymongo.domain.MongoConnect;

/**
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoClientUtil {

    public static MongoClient newClient(MongoConnect connect) {
        return new MongoClient(connect);
    }
}
