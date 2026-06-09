package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MysqlExecuteResult;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * db查询tab
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MysqlQuerySelectTab extends RichTab {

    {
        this.setClosable(false);
    }

    @Override
    protected String url() {
        return MongoTab.BASE_PATH + "query/mysqlQuerySelectTab.fxml";
    }

    public void init(String title, MysqlExecuteResult result, MongoDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public MysqlQuerySelectTabController controller() {
        return (MysqlQuerySelectTabController) super.controller();
    }
}
