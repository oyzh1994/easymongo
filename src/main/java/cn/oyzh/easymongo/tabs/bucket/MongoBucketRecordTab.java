package cn.oyzh.easymongo.tabs.bucket;

import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.tabs.MongoTab;
import cn.oyzh.easymongo.trees.database.MongoDatabaseTreeItem;
import cn.oyzh.easymongo.trees.bucket.MongoBucketTreeItem;
import cn.oyzh.fx.gui.svg.glyph.BucketSVGGlyph;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.Cursor;

import java.util.List;

/**
 * 桶记录标签页
 *
 * @author oyzh
 * @since 2023/12/24
 */
public class MongoBucketRecordTab extends MongoTab {

    @Override
    protected String url() {
        return FXConst.TAB_PATH + "bucket/mongoBucketRecordTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new BucketSVGGlyph();
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    @Override
    public void flushTitle() {
        // 设置提示文本
        this.setText(this.item().bucketName() + "@" + this.item().dbName() + "(" + this.item().infoName() + ")");
    }

    /**
     * 初始化
     *
     * @param item 树键
     * @return 是否初始化成功
     */
    public boolean init(MongoBucketTreeItem item) {
        this.controller().init(item);
        // 刷新tab
        this.flush();
        // 加载耗时处理
        return true;
    }

    @Override
    public MongoBucketRecordTabController controller() {
        return (MongoBucketRecordTabController) super.controller();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public MongoClient client() {
        return this.item().client();
    }

    /**
     * 设置过滤条件
     *
     * @param filters 过滤条件列表
     */
    public void setFilters(List<MongoRecordFilter> filters) {
        this.controller().setFilters(filters);
    }

    /**
     * 获取桶树节点
     *
     * @return 桶树节点
     */
    public MongoBucketTreeItem item(){
        return this.controller().getItem();
    }

    /**
     * 获取桶名称
     *
     * @return 桶名称
     */
    public String bucketName() {
        return this.item().bucketName();
    }

    @Override
    public MongoDatabaseTreeItem dbItem() {
        return this.item().dbItem();
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        return this.item().dbName();
    }

//    @Override
//    public void initNode() {
//        this.setClosable(true);
//        super.initNode();
//    }
}
