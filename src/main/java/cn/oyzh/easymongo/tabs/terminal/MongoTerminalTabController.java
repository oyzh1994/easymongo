package cn.oyzh.easymongo.tabs.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.terminal.MongoTerminalPane;
import cn.oyzh.fx.gui.tabs.RichTabController;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * redis命令行tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class MongoTerminalTabController extends RichTabController {

    /**
     * redis命令行文本域
     */
    @FXML
    private MongoTerminalPane terminal;

    private String dbName;

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(MongoClient client, String dbName) {
        this.terminal.init(client,dbName);
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    protected MongoConnect shellConnect() {
        return this.terminal.shellConnect();
    }

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
