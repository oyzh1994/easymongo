package cn.oyzh.easymongo.event.connect;

import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class DBConnectionClosedEvent extends Event<MongoClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().connectName());
    }

    public MongoConnect dbConnect() {
        return this.data().getDbConnect();
    }
}
