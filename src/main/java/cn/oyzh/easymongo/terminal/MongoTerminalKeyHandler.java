package cn.oyzh.easymongo.terminal;

import cn.oyzh.fx.terminal.key.TerminalKeyHandler;

/**
 * Mongo 终端按键处理器
 *
 * @author oyzh
 * @since 2023/8/28
 */
public class MongoTerminalKeyHandler implements TerminalKeyHandler<MongoTerminalPane> {

    /**
     * 当前实例
     */
    public static final MongoTerminalKeyHandler INSTANCE = new MongoTerminalKeyHandler();

}
