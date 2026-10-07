package cn.oyzh.easymongo.tabs;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * Mongo 标签页基类
 *
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class MongoTab extends RichTab {

    /**
     * 获取数据库树节点
     *
     * @return 数据库树节点
     */
    public abstract MongoDatabaseTreeItem dbItem() ;
}
