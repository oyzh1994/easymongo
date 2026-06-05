package cn.oyzh.easymongo.fx;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class ShellMysqlDatabaseComboBox extends FXComboBox<String> {

    public void init(MongoClient client) {
        this.init(client, null);
    }

    public void init(MongoClient client, String dbName) {
        this.clearItems();
        List<MongoDatabase> databases = client.databases();
        if (CollectionUtil.isNotEmpty(databases)) {
            this.setItem(databases.stream().map(MongoDatabase::getName).toList());
        }
        if (dbName != null) {
            this.select(dbName);
        }
    }
}
