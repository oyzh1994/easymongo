package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;

import java.util.List;

/**
 * MongoDB集合选择框
 *
 * @author oyzh
 * @since 2024/01/25
 */
public class ShellMongoCollectionComboBox extends FXComboBox<String> {

    /**
     * 初始化集合列表
     *
     * @param dbName 数据库名称
     * @param client Mongo客户端
     */
    public void init(String dbName, MongoClient client) {
        this.init(dbName, null, client);
    }

    /**
     * 初始化集合列表并选中指定集合
     *
     * @param dbName    数据库名称
     * @param tableName 集合名称
     * @param client    Mongo客户端
     */
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
