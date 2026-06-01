package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.exception.MongoException;
import com.mongodb.client.MongoClients;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2023/11/06
 */
public class MongoClient implements Closeable {

    /**
     * db信息
     */
    protected MongoConnect shellConnect;

    public MongoClient(MongoConnect value) {
        this.shellConnect = value;
    }

    public String connectName() {
        return this.shellConnect.getName();
    }

    public MongoConnect getDbConnect() {
        return this.shellConnect;
    }

    public boolean isConnected() {
        return this.state.get() != null && this.state.get().isConnected();
    }

    public boolean isConnecting() {
        return this.state.get() == MongoConnState.CONNECTING;
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
        try {
            if (this.mongoClient != null) {
                this.mongoClient.close();
            }
            this.state.set(MongoConnState.CLOSED);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("Zookeeper client close error.", ex);
        }
    }

    /**
     * 连接状态
     */
    private final SimpleObjectProperty<MongoConnState> state = new SimpleObjectProperty<>();

    /**
     * 当前状态监听器
     */
    private final ChangeListener<MongoConnState> stateListener = (state1, state2, state3) -> {
    };

    public ObjectProperty<MongoConnState> stateProperty() {
        return this.state;
    }


    /**
     * 初始化连接
     *
     * @return 连接
     */
    private String initHost() {
        // 连接地址
        String host = this.shellConnect.hostIp() + ":" + this.shellConnect.hostPort();
        return host;
    }


    private com.mongodb.client.MongoClient mongoClient;

    /**
     * 初始化客户端
     */
    private void initClient() {
        // 连接信息
        String host = this.initHost();
        String connStr = "mongodb://";
        if (this.shellConnect.isPasswordAuth()) {
            connStr += this.shellConnect.getUser() + ":" + this.shellConnect.getPassword() + "@";
        }
        connStr += host;
        if (this.shellConnect.isPasswordAuth()) {
            connStr += "/" + this.shellConnect.getAuthDatabase();
        }
        // 创建客户端
        this.mongoClient = MongoClients.create(connStr);
    }

    public void start() {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        // 初始化客户端
        this.initClient();
        try {
            // 开始连接时间
            final AtomicLong starTime = new AtomicLong(System.currentTimeMillis());
            // 更新连接状态
            this.state.set(MongoConnState.CONNECTING);
            // 检查连接
            this.mongoClient.listDatabases();
            // 更新连接状态
            this.state.set(MongoConnState.CONNECTED);
            // 开始连接时间
            starTime.set(System.currentTimeMillis());
        } catch (Exception ex) {
            this.state.set(MongoConnState.FAILED);
            JulLog.warn("Mongo client start error", ex);
            throw new MongoException(ex);
        }
    }
}
