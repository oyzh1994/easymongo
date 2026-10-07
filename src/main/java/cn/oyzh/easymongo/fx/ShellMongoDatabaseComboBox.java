package cn.oyzh.easymongo.fx;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * MongoDB数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class ShellMongoDatabaseComboBox extends FXComboBox<String> {

    /**
     * 初始化数据库列表
     *
     * @param client Mongo客户端
     */
    public void init(MongoClient client) {
        this.init(client, null);
    }

    /**
     * 初始化数据库列表并选中指定数据库
     *
     * @param client Mongo客户端
     * @param dbName 数据库名称
     */
    public void init(MongoClient client, String dbName) {
        this.clearItems();
        List<String> databases = client.listDatabaseNames();
        if (CollectionUtil.isNotEmpty(databases)) {
            this.setItem(databases);
        }
        if (dbName != null) {
            this.select(dbName);
        }
    }
}
