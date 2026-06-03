package cn.oyzh.easymongo.util;

import cn.oyzh.easymongo.controller.collection.MongoDocumentAddController;
import cn.oyzh.easymongo.controller.database.MongoDatabaseAddController;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;

/**
 * mongo页面工厂
 *
 * @author oyzh
 * @since 2026-06-03
 */
public class MongoViewFactory {

    /**
     * 添加文档
     *
     * @return 页面
     */
    public static StageAdapter documentAdd() {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoDocumentAddController.class, StageManager.getFrontWindow());
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

    /**
     * 添加数据库
     *
     * @return 页面
     */
    public static StageAdapter databaseAdd(MongoConnectTreeItem treeItem) {
        try {
            StageAdapter adapter = StageManager.parseStage(MongoDatabaseAddController.class, treeItem.window());
            adapter.setProp("connectItem", treeItem);
            adapter.showAndWait();
            return adapter;
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return null;
    }

}
