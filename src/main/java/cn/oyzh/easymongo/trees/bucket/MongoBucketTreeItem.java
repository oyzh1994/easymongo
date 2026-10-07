package cn.oyzh.easymongo.trees.bucket;

import cn.oyzh.common.dto.Paging;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.mongo.MongoBucket;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.mongo.MongoSelectRecordParam;
import cn.oyzh.easymongo.trees.MongoTreeItem;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import org.bson.types.ObjectId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * db树表节点
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoBucketTreeItem extends MongoTreeItem<MongoBucketTreeItemValue> {

    /**
     * 当前值
     */
    private final MongoBucket value;

    /**
     * 构造bucket树节点
     *
     * @param table    bucket对象
     * @param treeView 树视图
     */
    public MongoBucketTreeItem(MongoBucket table, RichTreeView treeView) {
        super(treeView);
        this.value = table;
        this.setValue(new MongoBucketTreeItemValue(this));
    }

    @Override
    public MongoBucketsTreeItem parent() {
        return (MongoBucketsTreeItem) super.parent();
    }

    /**
     * 获取mongo客户端
     *
     * @return mongo客户端
     */
    public MongoClient client() {
        return this.parent().client();
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
     * 获取bucket名称
     *
     * @return bucket名称
     */
    public String bucketName() {
        return this.value.getName();
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
        FXMenuItem openBucket = MenuItemHelper.openBucket( this::onPrimaryDoubleClick);
        items.add(openBucket);
        FXMenuItem clearBucket = MenuItemHelper.clearBucket( this::clearBucket);
        items.add(clearBucket);
        FXMenuItem deleteBucket = MenuItemHelper.deleteBucket( this::delete);
        items.add(deleteBucket);
        return items;
    }

    /**
     * 清空集合
     */
    private void clearBucket() {
        if (MessageBox.confirm(I18nHelper.clearBucket() + "[" + this.bucketName() + "]")) {
            this.dbItem().clearBucket(this.bucketName());
            this.parent().reloadChild();
        }
    }

    @Override
    public void delete() {
        try {
            if (MessageBox.confirm(I18nHelper.deleteBucket() + "[" + this.bucketName() + "]")) {
                this.dbItem().dropBucket(this.bucketName());
                MongoEventUtil.bucketDropped(this, this.dbItem());
                this.remove();
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 获取database树节点
     *
     * @return database树节点
     */
    public MongoDatabaseTreeItem dbItem() {
        if (this.parent() == null) {
            return null;
        }
        return this.parent().parent();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String infoName() {
        return parent().infoName();
    }

    @Override
    public void onPrimaryDoubleClick() {
        MongoEventUtil.bucketOpen(this, this.dbItem());
    }

    @Override
    public void loadChild() {
    }

    @Override
    public void reloadChild() {
        this.clearChild();
        this.setLoaded(false);
        this.loadChild();
    }

    /**
     * 获取bucket值
     *
     * @return bucket值
     */
    public MongoBucket value() {
        return value;
    }

    /**
     * 获取bucket字段集
     *
     * @return bucket字段集
     */
    public MongoColumns bucketColumns() {
        return this.client().bucketColumns();
    }

    /**
     * 分页查询记录
     *
     * @param pageNo  页码
     * @param limit   每页数量
     * @param filters 过滤条件
     * @param columns 字段集
     * @return 分页记录
     */
    public Paging<MongoRecord> recordPage(long pageNo, long limit, List<MongoRecordFilter> filters, MongoColumns columns) {
        MongoSelectRecordParam param = new MongoSelectRecordParam();
        param.setLimit(limit);
        param.setFilters(filters);
        param.setColumns(columns);
        param.setDbName(this.dbName());
        param.setStart(pageNo * limit);
        param.setCollectionName(this.bucketName());
        List<MongoRecord> rows = this.client().selectBucketRecords(param);
        long count = this.client().selectBucketRecordCount(param);
        Paging<MongoRecord> paging = new Paging<>(rows, limit, count);
        paging.currentPage(pageNo);
        return paging;
    }

    /**
     * 上传记录
     *
     * @param file 文件
     * @return 记录id
     * @throws Exception 异常
     */
    public ObjectId uploadRecord(File file) throws Exception {
        return this.client().uploadBucketRecord(this.dbName(), this.bucketName(), file);
    }

    /**
     * 查询记录
     *
     * @param _id 记录id
     * @return 记录
     */
    public MongoRecord selectRecord(Object _id) {
        return this.client().selectBucketRecord(this.dbName(), this.bucketName(), _id);
    }

    /**
     * 下载记录
     *
     * @param _id  记录id
     * @param file 文件
     * @throws Exception 异常
     */
    public void downloadRecord(Object _id, String file) throws Exception {
        this.client().downloadBucketRecord(this.dbName(), this.bucketName(), _id, file);
    }

    /**
     * 删除记录
     *
     * @param _id 记录id
     * @return 删除数量
     */
    public long deleteRecord(Object _id) {
        return this.client().deleteBucketRecord(this.dbName(), this.bucketName(), _id);
    }

    /**
     * 删除记录
     *
     * @param record 记录
     * @return 删除数量
     */
    public long deleteRecord(MongoRecord record) {
        return this.deleteRecord(record._idValue());
    }

    /**
     * 更新记录
     *
     * @param record 记录
     * @return 更新数量
     */
    public long updateRecord(MongoRecord record) {
        return this.client().updateBucketRecord( record);
    }

    /**
     * 执行脚本
     *
     * @param script 脚本
     * @return 执行结果
     * @throws Exception 异常
     */
    public Object eval(String script) throws Exception {
        return this.client().eval(this.dbName(), script);
    }
}
