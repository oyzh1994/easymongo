package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoExecuteResult;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.FXConst;

/**
 * 查询结果标签页
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQuerySelectTab extends RichTab {

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "query/mongoQuerySelectTab.fxml";
    }

    /**
     * 初始化查询结果
     *
     * @param title  标题
     * @param result 执行结果
     * @param dbItem 数据库树节点
     */
    public void init(String title, MongoExecuteResult result, MongoDatabaseTreeItem dbItem) {
        this.setTitle(title);
        this.controller().init(result, dbItem);
    }

    @Override
    public MongoQuerySelectTabController controller() {
        return (MongoQuerySelectTabController) super.controller();
    }

    @Override
    public void initNode() {
        this.setClosable(false);
        super.initNode();
    }
}
