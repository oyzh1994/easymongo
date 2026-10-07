package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * 查询主标签页
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class MongoQueryMainTab extends MongoTab {

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "query/mongoQueryMainTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new QuerySVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        String queryName = this.query().getName();
        if (queryName == null) {
            queryName = I18nHelper.newQuery();
        }
        // 设置提示文本
        if (this.controller().isUnsaved()) {
            this.setText("* " + queryName + "@" + this.dbName() + "(" + this.connectName() + ")");
        } else {
            this.setText(queryName + "@" + this.dbName() + "(" + this.connectName() + ")");
        }
    }

    /**
     * 获取查询对象
     *
     * @return 查询对象
     */
    public MongoQuery query() {
        return this.controller().getQuery();
    }

    /**
     * 获取查询标识
     *
     * @return 查询标识
     */
    public String queryId() {
        return this.query().getUid();
    }

    @Override
    public MongoDatabaseTreeItem dbItem() {
        return this.controller().getDbItem();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.dbItem().dbName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.dbItem().connectName();
    }

    /**
     * 初始化
     *
     * @param query 查询对象
     * @param item  db库树节点
     * @return 是否初始化成功
     */
    public boolean init(MongoQuery query, MongoDatabaseTreeItem item) {
        this.controller().init(this, query, item);
        this.flush();
        return true;
    }

    @Override
    public MongoQueryMainTabController controller() {
        return (MongoQueryMainTabController) super.controller();
    }

//    @Override
//    public void initNode() {
//        this.setClosable(true);
//        super.initNode();
//    }
}
