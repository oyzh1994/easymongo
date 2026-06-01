package cn.oyzh.easymongo.event.connect;

import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.easymongo.domain.MongoConnect;

/**
 * @author oyzh
 * @since 2024/01/30
 */
public class MongoConnectUpdatedEvent extends Event<MongoConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已修改", this.data().getName());
    }
}
