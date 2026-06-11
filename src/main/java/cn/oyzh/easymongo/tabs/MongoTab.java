package cn.oyzh.easymongo.tabs;

import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.tabs.RichTab;

/**
 * @author oyzh
 * @since 2024-09-12
 */
public abstract class MongoTab extends RichTab {

    public abstract MongoDatabaseTreeItem dbItem() ;
}
