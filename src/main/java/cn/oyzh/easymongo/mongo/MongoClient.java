package cn.oyzh.easymongo.mongo;

import cn.oyzh.easymongo.domain.MongoConnect;

import java.util.List;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2023/11/06
 */
public class MongoClient {

    /**
     * db信息
     */
    protected MongoConnect dbConnect;

    public MongoClient(MongoConnect value) {
        this.dbConnect = value;
    }

    public String connectName() {
        return this.dbConnect.getName();
    }

    public MongoConnect getDbConnect() {
        return this.dbConnect;
    }

    public boolean isConnected() {
        return false;
    }

    public boolean isConnecting() {
        return false;
    }

    public MongoDatabase database(String databaseName) {
        return null;
    }

    public List<MongoDatabase> databases() {
        return null;
    }

    public void createDatabase(MongoDatabase database) {
    }

    public boolean existDatabase(String dbName) {
        return false;
    }

    public boolean alterDatabase(MongoDatabase database) {
        return false;
    }

    public boolean dropDatabase(String dbName) {
        return false;
    }

    public void close() {
    }

    public void start() {

    }
}
