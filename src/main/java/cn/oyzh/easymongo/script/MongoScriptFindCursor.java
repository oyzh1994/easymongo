package cn.oyzh.easymongo.script;

import com.mongodb.client.FindIterable;
import org.bson.Document;

/**
 * 脚本环境中的查询游标封装，在通用游标基础上支持分页与执行计划分析
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoScriptFindCursor extends MongoScriptCursor {

    /** 所属数据库名称 */
    private final String dbName;

    /** 集合名称 */
    private final String collectionName;

    /** 底层查询结果集 */
    private final FindIterable<Document> cursor;

    /**
     * 构造查询游标封装
     *
     * @param dbName         所属数据库名称
     * @param collectionName 集合名称
     * @param cursor         底层查询结果集
     */
    public MongoScriptFindCursor(String dbName, String collectionName, FindIterable<Document> cursor) {
        super(cursor);
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.cursor = cursor;
    }

    /**
     * 获取所属数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * 限制返回的文档数量
     *
     * @param n 返回的文档数量上限
     * @return 新的查询游标
     */
    public MongoScriptFindCursor limit(int n) {
        return new MongoScriptFindCursor(this.dbName, this.collectionName, this.cursor.limit(n));
    }

    /**
     * 跳过指定数量的文档
     *
     * @param n 跳过的文档数量
     * @return 新的查询游标
     */
    public MongoScriptFindCursor skip(int n) {
        return new MongoScriptFindCursor(this.dbName, this.collectionName, this.cursor.skip(n));
    }

    /**
     * 获取查询的执行计划
     *
     * @return 执行计划文档
     */
    public Document explain() {
       return this.cursor.explain();
    }
}