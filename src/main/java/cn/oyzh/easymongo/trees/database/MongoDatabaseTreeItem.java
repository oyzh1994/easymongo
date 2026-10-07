package cn.oyzh.easymongo.trees.database;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoDatabase;
import cn.oyzh.easymongo.mongo.MongoFunction;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.query.MongoExecuteResult;
import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.bucket.MongoBucketsTreeItem;
import cn.oyzh.easymongo.trees.collection.MongoCollectionsTreeItem;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;
import cn.oyzh.easymongo.trees.function.ShellMongoFunctionsTreeItem;
import cn.oyzh.easymongo.trees.query.MongoQueriesTreeItem;
import cn.oyzh.easymongo.trees.terminal.MongoTerminalTreeItem;
import cn.oyzh.easymongo.util.MongoViewFactory;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.bson.BsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * db树database节点
 *
 * @author oyzh
 * @since 2023/12/12
 */
public class MongoDatabaseTreeItem extends MongoTreeItem<MongoDatabaseTreeItemValue> {

    /**
     * 当前值
     */
    private final MongoDatabase value;

    /**
     * 获取当前数据库
     *
     * @return 当前数据库
     */
    public MongoDatabase value() {
        return value;
    }

    /**
     * 构造database树节点
     *
     * @param database 数据库
     * @param treeView 树视图
     */
    public MongoDatabaseTreeItem(MongoDatabase database, RichTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(true);
        this.value = database;
        this.setValue(new MongoDatabaseTreeItemValue(this));
    }

    @Override
    public MongoConnectTreeItem parent() {
        return (MongoConnectTreeItem) super.parent();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.value.getName();
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String userName() {
        return this.info().getUser();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (!this.isChildEmpty()) {
            FXMenuItem closeDB = MenuItemHelper.closeDatabase(this::closeDB);
            items.add(closeDB);
        }
        //        FXMenuItem editDB = MenuItemHelper.editDatabase( this::editDB);
        //        items.add(editDB);
        FXMenuItem dropDB = MenuItemHelper.deleteDatabase(this::delete);
        items.add(dropDB);
        FXMenuItem dumpData = MenuItemHelper.dumpData(this::dump);
        items.add(dumpData);
        FXMenuItem runScriptFile = MenuItemHelper.runScriptFile(this::runScriptFile);
        items.add(runScriptFile);
        return items;
    }

    /**
     * 转储
     */
    private void dump() {
        MongoViewFactory.dumpData(this.client(), this.dbName(), null, 1);
    }

    /**
     * 运行脚本文件
     */
    private void runScriptFile() {
        MongoViewFactory.runScriptFile(this.client(), this.dbName());
    }

    @Override
    public void delete() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    if (MessageBox.confirm(I18nHelper.deleteDatabase() + "[" + this.dbName() + "]")) {
                        if (this.parent().dropDatabase(this.dbName())) {
                            MongoEventUtil.databaseDropped(this);
                            super.remove();
                        } else {
                            MessageBox.warn(I18nHelper.operationFail());
                        }
                    }
                })
                .onSuccess(super::refresh)
                .build();
        super.startWaiting(task);
    }

    //    /**
    //     * 编辑数据库
    //     */
    //    public void editDB() {
    //        StageAdapter fxView = StageManager.parseStage(MongoDatabaseUpdateController.class, this.window());
    //        fxView.setProp("database", this.value);
    //        fxView.setProp("connectItem", this.parent());
    //        fxView.display();
    //    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        this.clearChild();
        this.collapse();
        this.setLoaded(false);
        MongoEventUtil.databaseClosed(this);
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            this.setLoaded(true);
            this.setLoading(true);
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        List<TreeItem<?>> typeItems = new ArrayList<>();
                        typeItems.add(new MongoCollectionsTreeItem(this.getTreeView()));
                        typeItems.add(new MongoBucketsTreeItem(this.getTreeView()));
                        typeItems.add(new ShellMongoFunctionsTreeItem(this.getTreeView()));
                        typeItems.add(new MongoQueriesTreeItem(this.getTreeView()));
                        typeItems.add(new MongoTerminalTreeItem(this.getTreeView()));
                        super.setChild(typeItems);
                    })
                    .onSuccess(this::expend)
                    .onError(ex -> {
                        this.setLoaded(false);
                        MessageBox.error(ex.getMessage());
                    })
                    .onFinish(() -> this.setLoading(false))
                    .build();
            super.startWaiting(task);
        }

    }

    /**
     * 获取查询类型子节点
     *
     * @return 查询类型子节点
     */
    public MongoQueriesTreeItem getQueryTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof MongoQueriesTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取函数类型子节点
     *
     * @return 函数类型子节点
     */
    public ShellMongoFunctionsTreeItem getFunctionTypeChild() {
        for (RichTreeItem<?> child : this.richChildren()) {
            if (child instanceof ShellMongoFunctionsTreeItem treeItem) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取db客户端
     *
     * @return db客户端
     */
    public MongoClient client() {
        return this.parent().getClient();
    }

    /**
     * 获取db信息
     *
     * @return db信息
     */
    public MongoConnect info() {
        return this.parent().value();
    }

    /**
     * 获取连接信息名称
     *
     * @return 连接信息名称
     */
    public String infoName() {
        return this.info().getName();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.info().getName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    @Override
    public boolean itemVisible() {
        return this.isVisible();
    }

    //@Override
    //public synchronized void doFilter(RichTreeItemFilter itemFilter) {
    //    super.doFilter(itemFilter);
    //    this.refresh();
    //}

    /**
     * 获取shell连接
     *
     * @return shell连接
     */
    public MongoConnect shellConnect() {
        return this.client().getShellConnect();
    }

    /**
     * 删除集合
     *
     * @param collectionName 集合名称
     */
    public void dropCollection(String collectionName) {
        this.client().dropCollection(this.dbName(), collectionName);
    }

    /**
     * 清空集合数据
     *
     * @param collectionName 集合名称
     */
    public void clearCollection(String collectionName) {
        this.client().clearCollection(this.dbName(), collectionName);
    }

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     */
    public void dropBucket(String bucketName) {
        this.client().dropBucket(this.dbName(), bucketName);
    }

    /**
     * 清空桶数据
     *
     * @param bucketName 桶名称
     */
    public void clearBucket(String bucketName) {
        this.client().clearBucket(this.dbName(), bucketName);
    }

    /**
     * 执行单条脚本
     *
     * @param script 脚本内容
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public MongoExecuteResult executeSingleScript(String script) throws Exception {
        return this.client().executeSingleScript(this.dbName(), script);
    }

    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @return 执行结果集
     */
    public MongoQueryResults<MongoExecuteResult> executeScript(String script) {
        return this.client().executeScript(this.dbName(), script);
    }

    /**
     * 删除集合记录
     *
     * @param record 记录
     * @return 受影响记录数
     */
    public long deleteCollectionRecord(MongoRecord record) {
        return this.client().deleteCollectionRecord(record);
    }

    /**
     * 更新集合记录
     *
     * @param record 记录
     * @return 受影响记录数
     */
    public long updateCollectionRecord(MongoRecord record) {
        return this.client().updateCollectionRecord(record);
    }

    /**
     * 插入集合记录
     *
     * @param record 记录
     * @return 插入结果
     */
    public BsonValue insertCollectionRecord(MongoRecord record) {
        return this.client().insertCollectionRecord(record);
    }

    /**
     * 查询集合记录
     *
     * @param collectionName 集合名称
     * @param id 记录id
     * @return 集合记录
     */
    public MongoRecord selectCollectionRecord(String collectionName, Object id) {
        return this.client().selectCollectionRecord(this.dbName(), collectionName, id);
    }

    /**
     * 删除函数
     *
     * @param value 函数
     */
    public void dropFunction(MongoFunction value) {
        this.client().dropFunction(this.dbName(), value.getName());
    }

    /**
     * 重命名函数
     *
     * @param oldName 原名称
     * @param newName 新名称
     */
    public void renameFunction(String oldName, String newName) {
        this.client().renameFunction(this.dbName(), oldName, newName);
    }

    /**
     * 查询函数
     *
     * @param functionName 函数名称
     * @return 函数
     */
    public MongoFunction selectFunction(String functionName) {
        return this.client().selectFunction(this.dbName(), functionName);
    }

    /**
     * 创建函数
     *
     * @param function 函数
     */
    public void createFunction(MongoFunction function) {
        this.client().createFunction(this.dbName(), function.getName(), function.getCode());
    }

    /**
     * 修改函数
     *
     * @param function 函数
     */
    public void alertFunction(MongoFunction function) {
        this.client().alertFunction(this.dbName(), function.getName(), function.getCode());
    }

    /**
     * 重命名集合
     *
     * @param oldName 原名称
     * @param newName 新名称
     */
    public void renameCollection(String oldName, String newName) {
        this.client().renameCollection(this.dbName(), oldName, newName);
    }

    /**
     * 获取集合名称列表
     *
     * @return 集合名称列表
     */
    public List<String> listCollectionNames() {
        return this.client().listCollectionNames(this.dbName());
    }

    /**
     * 获取桶名称列表
     *
     * @return 桶名称列表
     */
    public List<String> listBucketNames() {
        return this.client().listBucketNames(this.dbName());
    }

    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public Object eval(String script) throws Exception {
        return this.client().eval(this.dbName(), script);
    }
}
