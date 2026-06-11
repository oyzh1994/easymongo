package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db查询信息tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQueryInfoTab extends RichTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MongoTab.BASE_PATH + "query/mysqlQueryInfoTab.fxml";
    }

    public void init(MongoQueryResults<?> results) {
        this.controller().init(results);
    }

    @Override
    public MongoQueryInfoTabController controller() {
        return (MongoQueryInfoTabController) super.controller();
    }
}
