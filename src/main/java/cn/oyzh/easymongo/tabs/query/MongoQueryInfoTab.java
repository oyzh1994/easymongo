package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.FXConst;

/**
 * 查询信息标签页
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQueryInfoTab extends RichTab {

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "query/mongoQueryInfoTab.fxml";
    }

    /**
     * 初始化查询结果信息
     *
     * @param results 查询结果集
     */
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
