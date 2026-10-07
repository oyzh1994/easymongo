package cn.oyzh.easymongo.trees.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.store.MongoQueryStore;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db树查询节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoQueryTreeItem extends MongoTreeItem<MongoQueryTreeItemValue> {

    /**
     * 当前值
     */
    private final MongoQuery value;

    /**
     * 获取查询对象
     *
     * @return 查询对象
     */
    public MongoQuery value() {
        return value;
    }

    /**
     * 构造db树查询节点
     *
     * @param query    查询对象
     * @param treeView 树视图
     */
    public MongoQueryTreeItem(MongoQuery query, RichTreeView treeView) {
        super(treeView);
        super.setFilterable(true);
        this.value = query;
        this.setValue(new MongoQueryTreeItemValue(this));
    }

    @Override
    public MongoQueriesTreeItem parent() {
        return (MongoQueriesTreeItem) super.parent();
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public MongoClient client() {
        return this.parent().client();
    }

    /**
     * 获取mongo连接信息
     *
     * @return mongo连接信息
     */
    public MongoConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openQuery = MenuItemHelper.openQuery(this::onPrimaryDoubleClick);
        items.add(openQuery);
        FXMenuItem renameQuery = MenuItemHelper.renameQuery(this::rename);
        items.add(renameQuery);
        FXMenuItem deleteQuery = MenuItemHelper.deleteTable(this::delete);
        items.add(deleteQuery);
        return items;
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " " + this.queryName() + "?")) {
            if (MongoQueryStore.INSTANCE.delete(this.value)) {
                this.remove();
                MongoEventUtil.queryDeleted(this);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String name = MessageBox.prompt(I18nHelper.pleaseInputName(), this.queryName());
        // 名称为null或者跟当前名称相同，则忽略
        if (name == null || Objects.equals(name, this.queryName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(name)) {
            MessageBox.warn(I18nHelper.pleaseInputName());
            return;
        }
        String oldName = this.value.getName();
        this.value.setName(name);
        // 修改名称
        if (MongoQueryStore.INSTANCE.update(this.value)) {
            MongoEventUtil.queryRenamed(this.value.getUid(), oldName, name, this.dbItem());
            this.refresh();
        } else {
            this.value.setName(oldName);
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 获取所属数据库节点
     *
     * @return 数据库节点
     */
    public MongoDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.parent().dbName();
    }

    /**
     * 获取查询名称
     *
     * @return 查询名称
     */
    public String queryName() {
        return this.value.getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.queryOpen(this.value, this.dbItem());
    }

    /**
     * 获取shell连接
     *
     * @return shell连接
     */
    public MongoConnect shellConnect() {
        return this.client().getShellConnect();
    }
}
