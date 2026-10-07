package cn.oyzh.easymongo.terminal;

import cn.oyzh.fx.terminal.mouse.TerminalMouseHandler;

/**
 * Mongo 终端鼠标处理器
 *
 * @author oyzh
 * @since 2023/8/28
 */
public class MongoTerminalMouseHandler implements TerminalMouseHandler<MongoTerminalPane> {

    /**
     * 当前实例
     */
    public static final MongoTerminalMouseHandler INSTANCE = new MongoTerminalMouseHandler();

}
