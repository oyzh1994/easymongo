package cn.oyzh.easymongo.event.connect;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/11/28
 */
public class MongoConnectionClosedEvent extends Event<MongoClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] closed", I18nHelper.connect(), this.data().connectName());
    }

    public MongoConnect shellConnect() {
        return this.data().getShellConnect();
    }
}
