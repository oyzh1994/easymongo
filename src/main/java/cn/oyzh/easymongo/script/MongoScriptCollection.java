package cn.oyzh.easymongo.script;

import cn.oyzh.common.json.JSONUtil;
import com.mongodb.MongoNamespace;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 脚本环境中的 MongoDB 集合操作封装，向脚本提供集合级的增删改查、索引、聚合等接口
 *
 * @author oyzh
 * @since 2026-06-06
 */
public class MongoScriptCollection {

    /** 所属数据库名称 */
    private final String dbName;

    /** 集合名称 */
    private final String collectionName;

    /** 底层 MongoDB 集合对象 */
    private final MongoCollection<Document> collection;

    /**
     * 构造集合操作封装
     *
     * @param collection 底层 MongoDB 集合对象
     */
    public MongoScriptCollection(MongoCollection<Document> collection) {
        this.dbName = collection.getNamespace().getDatabaseName();
        this.collectionName = collection.getNamespace().getCollectionName();
        this.collection = collection;
    }

    /**
     * 查询集合文档，使用空过滤条件
     *
     * @return 查询游标
     */
    public MongoScriptFindCursor find() {
        return this.find(null);
    }

    /**
     * 按过滤条件查询集合文档
     *
     * @param doc 过滤条件
     * @return 查询游标
     */
    public MongoScriptFindCursor find(Object doc) {
        Document filter = MongoScriptUtil.toDocumentOrDefault(doc);
        FindIterable<Document> iter = this.collection.find(filter);
        return new MongoScriptFindCursor(this.dbName, this.collectionName, iter);
    }

    /**
     * 插入文档，传入数组或集合时批量插入，单个文档时插入一条
     *
     * @param doc 待插入的文档或文档集合
     * @return 插入结果
     */
    public Object insert(Object doc) {
        if (doc instanceof ScriptObjectMirror mirror && mirror.isArray()) {
            return this.insertMany(mirror.values());
        }
        if (doc instanceof Collection<?> c) {
            return this.insertMany(c);
        }
        return this.insertOne(doc);
    }

    /**
     * 插入单个文档
     *
     * @param doc 待插入的文档
     * @return 单条插入结果
     */
    public InsertOneResult insertOne(Object doc) {
        Document document = MongoScriptUtil.toDocument(doc);
        return document != null ? this.collection.insertOne(document) : null;
    }

    /**
     * 批量插入文档
     *
     * @param doc 待插入的文档集合
     * @return 批量插入结果
     */
    public InsertManyResult insertMany(Object doc) {
        if (doc instanceof ScriptObjectMirror mirror) {
            return this.insertMany(mirror.values());
        }
        if (doc instanceof Collection<?> collection) {
            List<Document> list = new ArrayList<>();
            for (Object o : collection) {
                Document d = MongoScriptUtil.toDocument(o);
                if (d != null) {
                    list.add(d);
                }
            }
            return this.collection.insertMany(list);
        }
        return null;
    }

    /**
     * 删除一条符合过滤条件的文档
     *
     * @param doc 过滤条件
     * @return 删除结果
     */
    public DeleteResult delete(Object doc) {
        return this.deleteOne(doc);
    }

    /**
     * 删除一条符合过滤条件的文档
     *
     * @param doc 过滤条件
     * @return 删除结果
     */
    public DeleteResult deleteOne(Object doc) {
        Document filter = MongoScriptUtil.toDocument(doc);
        return filter != null ? this.collection.deleteOne(filter) : null;
    }

    /**
     * 删除集合全部文档
     *
     * @return 删除结果
     */
    public DeleteResult deleteMany() {
        return this.deleteMany(null);
    }

    /**
     * 删除全部符合过滤条件的文档
     *
     * @param doc 过滤条件
     * @return 删除结果
     */
    public DeleteResult deleteMany(Object doc) {
        Document filter = MongoScriptUtil.toDocumentOrDefault(doc);
        return this.collection.deleteMany(filter);
    }

    /**
     * 更新一条符合过滤条件的文档
     *
     * @param filter 过滤条件
     * @param doc    更新内容
     * @return 更新结果
     */
    public UpdateResult update(Object filter, Object doc) {
        return this.updateOne(filter, doc);
    }

    /**
     * 更新一条符合过滤条件的文档
     *
     * @param filter 过滤条件
     * @param doc    更新内容
     * @return 更新结果
     */
    public UpdateResult updateOne(Object filter, Object doc) {
        Document filterDoc = MongoScriptUtil.toDocument(filter);
        Document updateDoc = MongoScriptUtil.toDocument(doc);
        if (filterDoc != null && updateDoc != null) {
            return this.collection.updateOne(filterDoc, updateDoc);
        }
        return null;
    }

    /**
     * 删除当前集合
     */
    public void drop() {
        this.collection.drop();
    }

    // --- findOne ---

    /**
     * 查询集合中的第一条文档
     *
     * @return 第一条文档，无数据时返回 null
     */
    public Document findOne() {
        return this.collection.find().first();
    }

    /**
     * 查询符合过滤条件的第一条文档
     *
     * @param filter 过滤条件
     * @return 第一条文档，无数据时返回 null
     */
    public Document findOne(Object filter) {
        Document f = MongoScriptUtil.toDocument(filter);
        return f != null ? this.collection.find(f).first() : this.collection.find().first();
    }

    // --- findOneAndDelete ---

    /**
     * 查询并删除一条符合过滤条件的文档
     *
     * @param filter 过滤条件
     * @return 被删除的文档，未删除时返回 null
     */
    public Document findOneAndDelete(Object filter) {
        Document f = MongoScriptUtil.toDocument(filter);
        return f != null ? this.collection.findOneAndDelete(f) : null;
    }

    // --- findOneAndReplace ---

    /**
     * 查询并替换一条符合过滤条件的文档
     *
     * @param filter      过滤条件
     * @param replacement 替换后的文档内容
     * @return 替换前的文档，未替换时返回 null
     */
    public Document findOneAndReplace(Object filter, Object replacement) {
        Document f = MongoScriptUtil.toDocument(filter);
        Document r = MongoScriptUtil.toDocument(replacement);
        if (f != null && r != null) {
            return this.collection.findOneAndReplace(f, r);
        }
        return null;
    }

    // --- findOneAndUpdate ---

    /**
     * 查询并更新一条符合过滤条件的文档
     *
     * @param filter 过滤条件
     * @param update 更新内容
     * @return 更新前的文档，未更新时返回 null
     */
    public Document findOneAndUpdate(Object filter, Object update) {
        Document f = MongoScriptUtil.toDocument(filter);
        Document u = MongoScriptUtil.toDocument(update);
        if (f != null && u != null) {
            return this.collection.findOneAndUpdate(f, u);
        }
        return null;
    }

    // --- updateMany ---

    /**
     * 更新全部符合过滤条件的文档
     *
     * @param filter 过滤条件
     * @param update 更新内容
     * @return 更新结果
     */
    public UpdateResult updateMany(Object filter, Object update) {
        Document f = MongoScriptUtil.toDocument(filter);
        Document u = MongoScriptUtil.toDocument(update);
        if (f != null && u != null) {
            return this.collection.updateMany(f, u);
        }
        return null;
    }

    // --- replaceOne ---

    /**
     * 替换一条符合过滤条件的文档
     *
     * @param filter      过滤条件
     * @param replacement 替换后的文档内容
     * @return 更新结果
     */
    public UpdateResult replaceOne(Object filter, Object replacement) {
        Document f = MongoScriptUtil.toDocument(filter);
        Document r = MongoScriptUtil.toDocument(replacement);
        if (f != null && r != null) {
            return this.collection.replaceOne(f, r);
        }
        return null;
    }

    /**
     * 按选项替换一条符合过滤条件的文档
     *
     * @param filter      过滤条件
     * @param replacement 替换后的文档内容
     * @param option      替换选项
     * @return 更新结果
     */
    public UpdateResult replaceOne(Object filter, Object replacement, Object option) {
        Document f = MongoScriptUtil.toDocument(filter);
        Document r = MongoScriptUtil.toDocument(replacement);
        if (f != null && r != null && option instanceof Map<?, ?> o) {
            ReplaceOptions options = JSONUtil.toBean(o, ReplaceOptions.class);
            return this.collection.replaceOne(f, r, options);
        }
        return null;
    }

    // --- countDocuments ---

    /**
     * 统计集合文档总数
     *
     * @return 文档总数
     */
    public long countDocuments() {
        return this.collection.countDocuments();
    }

    /**
     * 统计符合过滤条件的文档数量
     *
     * @param filter 过滤条件
     * @return 文档数量
     */
    public long countDocuments(Object filter) {
        Document f = MongoScriptUtil.toDocument(filter);
        return f != null ? this.collection.countDocuments(f) : this.collection.countDocuments();
    }

    // --- estimatedDocumentCount ---

    /**
     * 估算集合文档总数
     *
     * @return 估算的文档总数
     */
    public long estimatedDocumentCount() {
        return this.collection.estimatedDocumentCount();
    }

    // --- distinct ---

    /**
     * 查询指定字段的去重值
     *
     * @param fieldName 字段名称
     * @return 去重值游标
     */
    public MongoScriptCursor distinct(String fieldName) {
        return new MongoScriptCursor(this.collection.distinct(fieldName, String.class));
    }

    /**
     * 查询符合过滤条件的指定字段去重值
     *
     * @param fieldName 字段名称
     * @param filter    过滤条件
     * @return 去重值游标
     */
    public MongoScriptCursor distinct(String fieldName, Object filter) {
        Document f = MongoScriptUtil.toDocument(filter);
        if (f != null) {
            return new MongoScriptCursor(this.collection.distinct(fieldName, f, String.class));
        }
        return new MongoScriptCursor(this.collection.distinct(fieldName, String.class));
    }

    // --- aggregate ---

    /**
     * 执行聚合管道
     *
     * @param pipeline 聚合管道阶段
     * @return 聚合结果游标
     */
    public MongoScriptCursor aggregate(Object pipeline) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        AggregateIterable<Document> iter = this.collection.aggregate(stages);
        iter.allowDiskUse(true);
        return new MongoScriptCursor(iter);
    }

    // --- indexes ---

    /**
     * 创建索引
     *
     * @param keys 索引键
     * @return 索引名称
     */
    public String createIndex(Object keys) {
        return this.createIndex(keys, null);
    }

    /**
     * 按选项创建索引
     *
     * @param keys    索引键
     * @param options 索引选项
     * @return 索引名称
     */
    public String createIndex(Object keys, Object options) {
        Document k = MongoScriptUtil.toDocument(keys);
        if (k == null) {
            return null;
        }
        IndexOptions opts = new IndexOptions();
        if (options instanceof Map optMap) {
            if (optMap.containsKey("name")) {
                opts.name(optMap.get("name").toString());
            }
            if (optMap.containsKey("unique")) {
                opts.unique(Boolean.parseBoolean(optMap.get("unique").toString()));
            }
            if (optMap.containsKey("background")) {
                opts.background(Boolean.parseBoolean(optMap.get("background").toString()));
            }
            if (optMap.containsKey("sparse")) {
                opts.sparse(Boolean.parseBoolean(optMap.get("sparse").toString()));
            }
            if (optMap.containsKey("expireAfterSeconds")) {
                opts.expireAfter(Long.parseLong(optMap.get("expireAfterSeconds").toString()), TimeUnit.SECONDS);
            }
        }
        return this.collection.createIndex(k, opts);
    }

    /**
     * 查询集合的全部索引
     *
     * @return 索引游标
     */
    public MongoScriptCursor listIndexes() {
        return new MongoScriptCursor(this.collection.listIndexes());
    }

    /**
     * 按索引键删除索引
     *
     * @param keys 索引键
     */
    public void dropIndex(Object keys) {
        Document k = MongoScriptUtil.toDocument(keys);
        if (k != null) {
            this.collection.dropIndex(k);
        }
    }

    /**
     * 按索引名称删除索引
     *
     * @param name 索引名称
     */
    public void dropIndexByName(String name) {
        this.collection.dropIndex(name);
    }

    /**
     * 删除集合的全部索引
     */
    public void dropIndexes() {
        this.collection.dropIndexes();
    }

    // --- rename ---

    /**
     * 重命名当前集合
     *
     * @param newName 新的集合名称
     */
    public void rename(String newName) {
        this.collection.renameCollection(new MongoNamespace(this.dbName, newName));
    }
}
