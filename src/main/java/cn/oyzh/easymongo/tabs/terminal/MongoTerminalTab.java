package cn.oyzh.easymongo.tabs.terminal;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class MongoTerminalTab extends RichTab {

    public MongoTerminalTab(MongoClient client, String dbName) {
        this.init(client, dbName);
    }

    @Override
    public MongoTerminalTabController controller() {
        return (MongoTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "terminal/mongoTerminalTab.fxml";
    }

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph graphic = (TerminalSVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new TerminalSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    protected String getTabTitle() {
        MongoConnect redisConnect = this.redisConnect();
        return redisConnect.getName();
    }

    /**
     * 初始化
     *
     * @param client zk客户端
     */
    private void init(MongoClient client, String dbName) {
        try {
            if (client == null) {
                MongoConnect connect = new MongoConnect();
                connect.setName(I18nHelper.unnamedConnection());
                // 刷新图标
                this.flushGraphic();
                // 初始化zk连接
                this.controller().init(new MongoClient(connect), dbName);
            } else {
                // 刷新图标
                this.flushGraphic();
                // 初始化zk连接
                this.controller().init(client, dbName);
            }
            this.flushTitle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * redis信息
     *
     * @return 当前redis信息
     */
    public MongoConnect redisConnect() {
        return this.controller().shellConnect();
    }

    public MongoClient client() {
        return this.controller().client();
    }

    public String dbName() {
        return this.controller().getDbName();
    }
}
