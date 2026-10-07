package cn.oyzh.easymongo.query;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoRecordUtil;

import java.util.Collections;
import java.util.List;

/**
 * 查询结果
 *
 * @author oyzh
 * @since 2024/08/19
 */
public abstract class MongoQueryResult {

    /**
     * 脚本
     */
    protected String script;

    /**
     * 耗时，微妙
     */
    protected long used;

    /**
     * 消息
     */
    protected String msg;

    /**
     * 变更总数
     */
    protected long updateCount;

    /**
     * 是否成功
     */
    protected boolean success;

    /**
     * 字段列表
     */
    protected MongoColumns columns;

    /**
     * 行列表
     */
    protected List<MongoRecord> records;

    /**
     * 是否存在查询结果
     *
     * @return 是否存在查询结果
     */
    public boolean hasResult() {
        if (CollectionUtil.isNotEmpty(this.records)) {
            return true;
        }
        return this.columns == null || this.columns.isEmpty();
    }

    /**
     * 解析查询结果
     *
     * @param records 记录列表
     */
    public void parseResult(List<MongoRecord> records) {
        this.records = records;
        this.columns = MongoRecordUtil.columns(records);
    }

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String dbName() {
        if (this.columns != null) {
            for (MongoColumn column : this.columns) {
                return column.getDbName();
            }
        }
        return null;
    }

    /**
     * 获取集合名称
     *
     * @return 集合名称
     */
    public String collectionName() {
        if (this.columns != null) {
            for (MongoColumn column : this.columns) {
                return column.getCollectionName();
            }
        }
        return null;
    }

    /**
     * 获取主键列
     *
     * @return 主键列
     */
    public MongoColumn getPrimaryKey() {
        if (this.columns != null) {
            for (MongoColumn column : this.columns) {
                if (column.is_id()) {
                    return column;
                }
            }
        }
        return null;
    }

    /**
     * 是否可更新
     *
     * @return 是否可更新
     */
    public boolean isUpdatable() {
        if (this.columns != null) {
            for (MongoColumn column : this.columns) {
                if (column.is_id()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取结果行数
     *
     * @return 结果行数
     */
    public int getCount() {
        return this.records == null ? 0 : this.records.size();
    }

    /**
     * 获取耗时（毫秒）
     *
     * @return 耗时（毫秒）
     */
    public long getUsedMs() {
        return this.used / 1_000_000L;
    }

    /**
     * 获取列列表
     *
     * @return 列列表
     */
    public List<MongoColumn> columnList() {
        if (this.columns == null) {
            return Collections.emptyList();
        }
        return this.columns;
    }

    /**
     * 获取脚本
     *
     * @return 脚本
     */
    public String getScript() {
        return script;
    }

    /**
     * 设置脚本
     *
     * @param script 脚本
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * 获取耗时（微秒）
     *
     * @return 耗时（微秒）
     */
    public long getUsed() {
        return used;
    }

    /**
     * 设置耗时（微秒）
     *
     * @param used 耗时（微秒）
     */
    public void setUsed(long used) {
        this.used = used;
    }

    /**
     * 获取消息
     *
     * @return 消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置消息
     *
     * @param msg 消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取变更总数
     *
     * @return 变更总数
     */
    public long getUpdateCount() {
        return updateCount;
    }

    /**
     * 设置变更总数
     *
     * @param updateCount 变更总数
     */
    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    /**
     * 是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置是否成功
     *
     * @param success 是否成功
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取字段列表
     *
     * @return 字段列表
     */
    public MongoColumns getColumns() {
        return columns;
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void setColumns(MongoColumns columns) {
        this.columns = columns;
    }

    /**
     * 获取行列表
     *
     * @return 行列表
     */
    public List<MongoRecord> getRecords() {
        return records;
    }

    /**
     * 设置行列表
     *
     * @param records 行列表
     */
    public void setRecords(List<MongoRecord> records) {
        this.records = records;
    }
}
