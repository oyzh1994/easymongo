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
     * @param query db信息
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

    public boolean isNew() {
        return this.getUid() == null;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
