package cn.oyzh.easymongo.trees.function;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db树函数节点
 *
 * @author oyzh
 * @since 2024/06/29
 */
public class ShellMysqlFunctionTreeItem extends MongoTreeItem<ShellMysqlFunctionTreeItemValue> {

    /**
     * 当前值
     */
    private final MongoFunction value;

    public ShellMysqlFunctionTreeItem(MongoFunction function, RichTreeView treeView) {
        super(treeView);
        this.value = function;
        super.setFilterable(true);
        this.setValue(new ShellMysqlFunctionTreeItemValue(this));
    }

    @Override
    public ShellMysqlFunctionsTreeItem parent() {
        return (ShellMysqlFunctionsTreeItem) super.parent();
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
     * 获取redis信息
     *
     * @return redis信息
     */
    public MongoConnect info() {
        return this.parent().info();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem design = MenuItemHelper.designFunction("12", this::onPrimaryDoubleClick);
        items.add(design);
        FXMenuItem renameFunction = MenuItemHelper.renameFunction("12", this::rename);
        items.add(renameFunction);
        FXMenuItem delete = MenuItemHelper.deleteFunction("12", this::delete);
        items.add(delete);
        items.add(MenuItemHelper.separator());
        FXMenuItem cloneFunction = MenuItemHelper.cloneFunction("12", this::cloneFunction);
        items.add(cloneFunction);
        FXMenuItem info = MenuItemHelper.functionInfo("12", this::functionInfo);
        items.add(info);
        return items;
    }

    private void functionInfo() {
    }

    /**
     * 克隆函数
     */
    private void cloneFunction() {
    }

    @Override
    public void delete() {
        if (!MessageBox.confirm(I18nHelper.deleteFunction() + " " + this.value.getName() + "?")) {
            return;
        }
        try {
            MongoEventUtil.dropFunction(this);
            this.dbItem().dropFunction(this.value);
            super.remove();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    public MongoDatabaseTreeItem dbItem() {
        return this.parent().parent();
    }

    public String dbName() {
        return this.parent().dbName();
    }

    public String infoName() {
        return this.parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.designFunction(this.value, this.dbItem());
    }

    public String functionName() {
        return this.value.getName();
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    @Override
    public void loadChild() {
        try {
            this.setLoaded(true);
            this.setLoading(true);
            MongoFunction function = this.client().selectFunction(this.dbName(), this.functionName());
            if (function != null) {
                this.value.copy(function);
            }
        } catch (Exception ex) {
            this.setLoaded(false);
            ex.printStackTrace();
            MessageBox.exception(ex);
        } finally {
            this.setLoading(false);
        }
    }

    @Override
    public void onPrimarySingleClick() {
        if (!this.isLoaded()) {
            super.onPrimarySingleClick();
        } else {
            super.onPrimarySingleClick();
        }
    }

    public MongoFunction value() {
        return value;
    }

    @Override
    public void rename() {
        try {
            String newName = MessageBox.prompt(I18nHelper.pleaseInputName(), this.functionName());
            // 名称为null或者跟当前名称相同，则忽略
            if (newName == null || Objects.equals(newName, this.functionName())) {
                return;
            }
            // 检查名称
            if (StringUtil.isBlank(newName)) {
                MessageBox.warn(I18nHelper.pleaseInputContent());
                return;
            }
            String oldName = this.functionName();
            // 修改名称
            this.dbItem().renameFunction(oldName, newName);
            this.value.setName(newName);
            this.refresh();
            MongoEventUtil.functionRenamed(oldName,newName, this.dbItem());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }
}
