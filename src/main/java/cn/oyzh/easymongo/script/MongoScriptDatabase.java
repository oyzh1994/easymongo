package cn.oyzh.easymongo.script;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import org.bson.Document;

import java.util.List;
import java.util.Map;

/**
 * 脚本环境中的 MongoDB 数据库操作封装，向脚本提供数据库级的集合管理、命令执行、聚合与监听等接口
 *
 * @author oyzh
 * @since 2026-06-06
 */
public class MongoScriptDatabase {

    /** 底层 MongoDB 数据库对象 */
    private final MongoDatabase database;

    /**
     * 构造数据库操作封装
     *
     * @param database 底层 MongoDB 数据库对象
     */
    public MongoScriptDatabase(MongoDatabase database) {
        this.database = database;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getName() {
        return this.database.getName();
    }

    /**
     * 获取指定集合的操作封装
     *
     * @param name 集合名称
     * @return 集合操作封装
     */
    public MongoScriptCollection getCollection(String name) {
        MongoCollection<Document> collection = this.database.getCollection(name);
        return new MongoScriptCollection(collection);
    }

    /**
     * 创建集合
     *
     * @param name 集合名称
     * @return 当前数据库对象
     */
    public MongoScriptDatabase createCollection(String name) {
        this.database.createCollection(name);
        return this;
    }

    /**
     * 按选项创建集合
     *
     * @param name    集合名称
     * @param options 创建选项
     * @return 当前数据库对象
     */
    public MongoScriptDatabase createCollection(String name, Object options) {
        CreateCollectionOptions opts = new CreateCollectionOptions();
        if (options instanceof Map optMap) {
            if (optMap.containsKey("capped")) {
                opts.capped(Boolean.parseBoolean(optMap.get("capped").toString()));
            }
            if (optMap.containsKey("sizeInBytes")) {
                opts.sizeInBytes(Long.parseLong(optMap.get("sizeInBytes").toString()));
            }
            if (optMap.containsKey("maxDocuments")) {
                opts.maxDocuments(Long.parseLong(optMap.get("maxDocuments").toString()));
            }
        }
        this.database.createCollection(name, opts);
        return this;
    }

    /**
     * 删除当前数据库
     */
    public void drop() {
        this.database.drop();
    }

    /**
     * 执行数据库命令
     *
     * @param command 命令内容
     * @return 命令执行结果，命令非法时返回 null
     */
    public Document runCommand(Object command) {
        if (command instanceof Map map) {
            return this.database.runCommand(new Document(map));
        }
        return null;
    }

    /**
     * 创建视图
     *
     * @param name     视图名称
     * @param viewOn   视图依赖的集合名称
     * @param pipeline 聚合管道阶段
     */
    public void createView(String name, String viewOn, Object pipeline) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        this.database.createView(name, viewOn, stages);
    }

    /**
     * 按选项创建视图
     *
     * @param name     视图名称
     * @param viewOn   视图依赖的集合名称
     * @param pipeline 聚合管道阶段
     * @param options  创建选项
     */
    public void createView(String name, String viewOn, Object pipeline, Object options) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        CreateViewOptions opts = new CreateViewOptions();
        this.database.createView(name, viewOn, stages, opts);
    }

    /**
     * 执行数据库级聚合管道
     *
     * @param pipeline 聚合管道阶段
     * @return 聚合结果游标
     */
    public MongoScriptCursor aggregate(Object pipeline) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        AggregateIterable<Document> iter = this.database.aggregate(stages);
        iter.allowDiskUse(true);
        return new MongoScriptCursor(iter);
    }

    /**
     * 监听数据库的变更流
     *
     * @return 变更流游标
     */
    public MongoScriptCursor watch() {
        ChangeStreamIterable<Document> iter = this.database.watch();
        return new MongoScriptCursor(iter);
    }

    /**
     * 按管道监听数据库的变更流
     *
     * @param pipeline 监听管道阶段
     * @return 变更流游标
     */
    public MongoScriptCursor watch(Object pipeline) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        ChangeStreamIterable<Document> iter = this.database.watch(stages);
        return new MongoScriptCursor(iter);
    }

    /**
     * 查询数据库下的全部集合名称
     *
     * @return 集合名称游标
     */
    public MongoScriptCursor listCollectionNames() {
        MongoIterable<String> iter = this.database.listCollectionNames();
        return new MongoScriptCursor(iter);
    }

    /**
     * 查询数据库下的全部集合信息
     *
     * @return 集合信息游标
     */
    public MongoScriptCursor listCollections() {
        ListCollectionsIterable<Document> iter = this.database.listCollections();
        return new MongoScriptCursor(iter);
    }
}
