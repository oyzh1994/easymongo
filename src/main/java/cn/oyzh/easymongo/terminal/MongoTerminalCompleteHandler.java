package cn.oyzh.easymongo.terminal;

import cn.oyzh.fx.terminal.Terminal;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.complete.BaseTerminalCompleteHandler;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;

import java.util.List;

/**
 * 终端提示器
 *
 * @author oyzh
 * @since 2023/7/24
 */
public class MongoTerminalCompleteHandler extends BaseTerminalCompleteHandler<MongoTerminalPane> {

    @Override
    protected List<TerminalCommandHandler<?, ?>> findCommandHandlers(MongoTerminalPane terminal, String line) {
        MongoTerminalPane terminalPane = terminal;
        TerminalCommandHandler<?, ?> commandHandler = new TerminalCommandHandler<>() {

            @Override
            public TerminalExecuteResult execute(TerminalCommand command, Terminal terminal) {
                String input = command.getCommand();
                return terminalPane.eval(input);
            }

            @Override
            public boolean completion(String input, Terminal terminal) {
                return false;
            }

            @Override
            public TerminalCommand parseCommand(String input) throws Exception {
                TerminalCommand command = new TerminalCommand();
                command.setCommand(input);
                return command;
            }

            @Override
            public String commandName() {
                return "";
            }
        };
        return List.of(commandHandler);
    }

    /**
     * 当前实例
     */
    public static final MongoTerminalCompleteHandler INSTANCE = new MongoTerminalCompleteHandler();

}
