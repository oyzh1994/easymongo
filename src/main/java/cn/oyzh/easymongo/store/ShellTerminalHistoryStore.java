package cn.oyzh.easymongo.store;

import cn.oyzh.easymongo.domain.ShellTerminalHistory;
import cn.oyzh.store.jdbc.JdbcStandardStore;

/**
 * 命令行终端历史存储
 *
 * @author oyzh
 * @since 2024-11-25
 */
public class ShellTerminalHistoryStore extends JdbcStandardStore<ShellTerminalHistory> {

    /**
     * 当前实例
     */
    public static final ShellTerminalHistoryStore INSTANCE = new ShellTerminalHistoryStore();

    /**
     * 替换终端历史，直接新增
     *
     * @param model 终端历史
     * @return 结果
     */
    public boolean replace(ShellTerminalHistory model) {
        return this.insert(model);
    }

    @Override
    protected Class<ShellTerminalHistory> modelClass() {
        return ShellTerminalHistory.class;
    }
}
