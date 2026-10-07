package cn.oyzh.easymongo.terminal.basic;

/**
 * show tables 终端命令处理器
 *
 * @author oyzh
 * @since 2023/09/20
 */
public class MongoShowTablesTerminalCommandHandler extends MongoShowCollectionsTerminalCommandHandler {

    @Override
    public String commandSubName() {
        return "tables";
    }

}
