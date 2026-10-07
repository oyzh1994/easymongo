package cn.oyzh.easymongo.domain;

import cn.oyzh.fx.terminal.histroy.TerminalHistory;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

/**
 * 命令行终端历史记录
 *
 * @author oyzh
 * @since 2024-11-25
 */
@Table("t_terminal_history")
public class ShellTerminalHistory extends TerminalHistory {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String tid;

    /**
     * 获取数据id
     *
     * @return 数据id
     */
    public String getTid() {
        return tid;
    }

    /**
     * 设置数据id
     *
     * @param tid 数据id
     */
    public void setTid(String tid) {
        this.tid = tid;
    }
}
