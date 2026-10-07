package cn.oyzh.easymongo.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * db查询
 *
 * @author oyzh
 * @since 2024/02/18
 */
@Table("t_query")
public class MongoQuery implements Serializable, Comparable<MongoQuery>, ObjectComparator<MongoQuery> {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * 连接id
     */
    @Column
    private String iid;

    /**
     * 数据库名称
     */
    @Column
    private String dbName;

    /**
     * 名称
     */
    @Column
    private String name;

    /**
     * 内容
     */
    @Column
    private String content;

    /**
     * 复制对象
     *
     * @param query 查询信息
     * @return 当前对象
     */
    public MongoQuery copy(MongoQuery query) {
        this.iid = query.iid;
        this.name = query.name;
        this.dbName = query.dbName;
        this.content = query.content;
        return this;
    }

    @Override
    public int compareTo(MongoQuery t1) {
        if (t1 == null) {
            return 1;
        }
        return StringUtil.compare(t1.uid, this.uid, true);
    }

    @Override
    public boolean compare(MongoQuery t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.uid, t1.uid);
    }

    /**
     * 是否为新建查询
     *
     * @return 结果
     */
    public boolean isNew() {
        return this.getUid() == null;
    }

    /**
     * 获取数据id
     *
     * @return 数据id
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置数据id
     *
     * @param uid 数据id
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取连接id
     *
     * @return 连接id
     */
    public String getIid() {
        return iid;
    }

    /**
     * 设置连接id
     *
     * @param iid 连接id
     */
    public void setIid(String iid) {
        this.iid = iid;
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * 设置数据库名称
     *
     * @param dbName 数据库名称
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取内容
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}
