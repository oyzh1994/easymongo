package cn.oyzh.easymongo.event.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.event.Event;

/**
 * MongoDB 终端关闭事件
 *
 * @author oyzh
 * @since 2023/11/20
 */
public class MongoTerminalCloseEvent extends Event<MongoClient> {

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置数据库名称
     *
     * @param dbName 数据库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
