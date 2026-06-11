package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * db数据库选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class ShellMongoCollectionComboBox extends FXComboBox<String> {

    public void init(String dbName, MongoClient client) {
        this.init(dbName, null, client);
    }

    public void init(String dbName, String tableName, MongoClient client) {
        List<MongoCollection> list = client.listCollections(dbName);
        this.setItem(list.parallelStream().map(MongoCollection::getName).toList());
        if (tableName != null) {
            this.select(tableName);
        } else {
            this.clearChild();
        }
    }
}
