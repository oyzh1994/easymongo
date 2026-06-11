package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoExecuteResult;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.FXConst;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQuerySelectTab extends RichTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "query/mongoQuerySelectTab.fxml";
    }

    public void init(String title, MongoExecuteResult result, MongoDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public MongoQuerySelectTabController controller() {
        return (MongoQuerySelectTabController) super.controller();
    }
}
