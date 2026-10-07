package cn.oyzh.easymongo.event.connect;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 连接已删除事件
 *
 * @author oyzh
 * @since 2024/7/26
 */
public class MongoConnectDeletedEvent extends Event<MongoConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] deleted", I18nHelper.connect(), this.data().getName());
    }
}
