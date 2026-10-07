package cn.oyzh.easymongo.terminal;

import cn.oyzh.fx.terminal.command.BaseTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;

/**
 * Mongo 终端命令处理器基类
 *
 * @author oyzh
 * @since 2023/7/31
 */
public abstract class MongoTerminalCommandHandler<C extends TerminalCommand> extends BaseTerminalCommandHandler<C, MongoTerminalPane> {

}
