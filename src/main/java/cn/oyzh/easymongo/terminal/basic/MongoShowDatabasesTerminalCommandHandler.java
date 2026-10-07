package cn.oyzh.easymongo.terminal.basic;

import cn.oyzh.easymongo.terminal.MongoTerminalCommandHandler;
import cn.oyzh.easymongo.terminal.MongoTerminalPane;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;

/**
 * show databases 终端命令处理器
 *
 * @author oyzh
 * @since 2023/09/20
 */
public class MongoShowDatabasesTerminalCommandHandler extends MongoShowDbsTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "databases";
    }

}
