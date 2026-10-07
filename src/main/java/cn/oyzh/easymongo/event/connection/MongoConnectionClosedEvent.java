package cn.oyzh.easymongo.event.connection;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * MongoDB 连接关闭事件
 *
 * @author oyzh
 * @since 2023/11/28
 */
public class MongoConnectionClosedEvent extends Event<MongoClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s] closed", I18nHelper.connect(), this.data().connectName());
    }

    /**
     * 获取 Shell 连接信息
     *
     * @return Shell 连接信息
     */
    public MongoConnect shellConnect() {
        return this.data().getShellConnect();
    }
}
