package cn.oyzh.easymongo.tabs.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.terminal.MongoTerminalPane;
import cn.oyzh.fx.gui.tabs.RichTabController;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * Mongo 终端标签页内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class MongoTerminalTabController extends RichTabController {

    /**
     * 终端组件
     */
    @FXML
    private MongoTerminalPane terminal;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 初始化
     *
     * @param client 客户端
     * @param dbName 数据库名称
     */
    public void init(MongoClient client, String dbName) {
        this.terminal.init(client,dbName);
        this.dbName = dbName;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 获取 Mongo 连接信息
     *
     * @return 当前 Mongo 连接信息
     */
    protected MongoConnect shellConnect() {
        return this.terminal.shellConnect();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public MongoClient client() {
        return this.terminal.getClient();
    }

    @Override
    public void onTabClosed(Event event) {
        if (this.terminal.isTemporary()) {
            this.client().close();
        }
        super.onTabClosed(event);
    }
}
