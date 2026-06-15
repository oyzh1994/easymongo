package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.FXConst;

/**
 * db查询信息tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQueryInfoTab extends RichTab {

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "query/mongoQueryInfoTab.fxml";
    }

    public void init(MongoQueryResults<?> results) {
        this.controller().init(results);
    }

    @Override
    public MongoQueryInfoTabController controller() {
        return (MongoQueryInfoTabController) super.controller();
    }

    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
