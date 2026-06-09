package cn.oyzh.easymongo.query;

/**
 * @author oyzh
 * @since 2024/02/19
 */
public class MysqlExecuteResult extends MysqlQueryResult {

    /**
     * 是否全字段
     */
    private boolean fullColumn;

    @Override
    public boolean hasResult() {
        if (this.updateCount > 0) {
            return false;
        }
        return super.hasResult();
    }

    public void setFullColumn(boolean fullColumn) {
        this.fullColumn = fullColumn;
    }

    public boolean isFullColumn() {
        return fullColumn;
    }
}
