package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.Destroyable;
import javafx.beans.value.ChangeListener;

import java.util.UUID;

/**
 * 数据库对象状态监听器
 *
 * @author oyzh
 * @since 2024/7/23
 */
public abstract class DBStatusListener implements ChangeListener<Object>, Destroyable {

    /**
     * 监听器标识
     */
    private final String key;

    /**
     * 使用随机标识构造监听器
     */
    public DBStatusListener() {
        this(UUID.randomUUID().toString()) ;
    }

    /**
     * 使用指定标识构造监听器
     *
     * @param key 监听器标识
     */
    public DBStatusListener(String key) {
        this.key = key;
        DBStatusListenerManager.addListener(this);
    }

    /**
     * 使用数据库名称、表名称构造监听器
     *
     * @param dbName    数据库名称
     * @param tableName 表名称
     */
    public DBStatusListener(String dbName, String tableName) {
        this(dbName + ":" + ":" + tableName);
    }

    /**
     * 使用数据库名称、模式名称、表名称构造监听器
     *
     * @param dbName    数据库名称
     * @param schema    模式名称
     * @param tableName 表名称
     */
    public DBStatusListener(String dbName, String schema, String tableName) {
        this(dbName + ":" + schema + ":" + tableName);
    }

//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//        DBStatusListenerManager.removeListener(this);
//    }

    @Override
    public void destroy() {
        DBStatusListenerManager.removeListener(this);
    }

    /**
     * 获取监听器标识
     *
     * @return 监听器标识
     */
    public String getKey() {
        return key;
    }
}
