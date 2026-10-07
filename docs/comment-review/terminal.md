# terminal 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/terminal/`
>
> 说明：本包实现内置 Mongo 命令行终端，各 Handler 继承 `cn.oyzh.fx.terminal` 下的终端框架基类。

## MongoTerminalCommandHandler

- 职责：Mongo 终端命令处理器抽象基类，泛型 `C extends TerminalCommand`，绑定终端类型为 `MongoTerminalPane`。

- 字段：（无）

- 方法：无自有方法（继承 `BaseTerminalCommandHandler<C, MongoTerminalPane>`）

- 调用链：`TerminalManager → MongoTerminalCommandHandler 子类.execute(command, terminal)`

## MongoTerminalCompleteHandler

- 职责：终端命令补全处理器，针对 `db.xxx()`、`db.getCollection('').xxx()` 等输入给出补全候选。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `collectionPattern` | `Pattern`（static） | 集合语句匹配模式（`^db\.getCollection\((['"])([^'"]*)\1\).*`） |
| `INSTANCE` | `MongoTerminalCompleteHandler`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `newCommandHandler(String name)` | 创建匿名命令处理器 | 执行时调用 `terminal.eval(command.getCommand())`，`commandName()` 返回给定名称 |
| `collectionPattern()`（static） | 获取集合语句正则 | 懒加载编译正则 |
| `findCommandHandlers(terminal, line)` | 查找补全候选 | 空行 → `db.getCollection('')`；匹配集合模式时按 `.` 数量（1 或 2）区分补全：`.` 数为 1 时用 `MongoScriptUtil.collectionFuncions()` 全部函数名补全，为 2 时按前缀匹配（`TextUtil.clacCorr > 0.3`）；`db` 开头时用 `MongoScriptUtil.databaseFuncions()` 同理补全；其余交给 `super` |
| `completion(line, terminal)` | 执行补全 | 候选为空 `noMatch`；恰好 1 个 `oneMatch`；多个 `multiMatch` |

- 调用链：
  - `MongoTerminalPane.initNode → completeHandler(INSTANCE) → completion → findCommandHandlers → MongoScriptUtil.collectionFuncions/databaseFuncions`
  - `候选执行 → newCommandHandler.execute → MongoTerminalPane.eval`

## MongoTerminalHelpHandler

- 职责：终端帮助处理器，继承 `BaseTerminalHelpHandler`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoTerminalHelpHandler`（static final） | 单例 |

- 方法：无自有方法

- 调用链：`MongoTerminalPane.initNode → helpHandler(INSTANCE)`

## MongoTerminalHistoryHandler

- 职责：终端历史处理器，负责历史的加载、添加与清空，并维护内存缓存。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoTerminalHistoryHandler`（static final） | 单例 |
| `cecheList` | `List<ShellTerminalHistory>`（final） | 内存历史缓存，初始容量 24 |
| `historyStore` | `ShellTerminalHistoryStore`（final） | 历史存储，取自 `INSTANCE` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `clearHistory()` | 清空历史 | `historyStore.clear()` + 清 `cecheList` |
| `listHistory()` | 列出历史 | 缓存为空时从 `historyStore.selectList()` 全量载入后返回缓存 |
| `addHistory(TerminalHistory)` | 添加历史 | 包装为 `ShellTerminalHistory`（设保存时间与行内容），`insert` 入库并加入缓存 |

- 调用链：`终端回车执行 → addHistory → ShellTerminalHistoryStore.insert`

## MongoTerminalKeyHandler

- 职责：终端按键处理器，实现 `TerminalKeyHandler<MongoTerminalPane>`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoTerminalKeyHandler`（static final） | 单例 |

- 方法：无自有方法

- 调用链：`MongoTerminalPane.initNode → keyHandler(INSTANCE)`

## MongoTerminalManager

- 职责：Mongo 终端管理器，集中注册终端命令处理器。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `registerHandlers()`（static） | 注册处理器 | 通过 `TerminalManager.registerHandler(TERMINAL_NAME, ...)` 注册标准命令 `HelpTerminalCommandHandler`、`ClearTerminalCommandHandler`，以及基础命令 `MongoShowDbsTerminalCommandHandler`、`MongoShowUseTerminalCommandHandler`、`MongoShowTablesTerminalCommandHandler`、`MongoShowDatabasesTerminalCommandHandler`、`MongoShowCollectionsTerminalCommandHandler` |

- 调用链：`EasyMongoApp.start → TerminalManager.setLoadHandler(TERMINAL_NAME, MongoTerminalManager::registerHandlers) → registerHandlers`

## MongoTerminalMouseHandler

- 职责：终端鼠标处理器，实现 `TerminalMouseHandler<MongoTerminalPane>`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoTerminalMouseHandler`（static final） | 单例 |

- 方法：无自有方法

- 调用链：`MongoTerminalPane.initNode → mouseHandler(INSTANCE)`

## MongoTerminalPane

- 职责：Mongo 终端文本域，继承 `TerminalPane`，封装 Mongo 客户端连接、提示符刷新、连接状态监听与脚本执行。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `TERMINAL_NAME` | `String`（static final） | 终端名称（值 `"zookeeper"`，用于终端框架注册键） |
| `client` | `MongoClient` | 关联的 Mongo 客户端 |
| `connectInfo` | `ShellMongoConnectInfo` | 由 `connect` 命令解析出的连接信息 |
| `stateChangeListener` | `ChangeListener<MongoConnState>` | 客户端连接状态监听器 |
| `dbName` | `String` | 当前数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getEditorFont()` | 编辑器字体 | 由 `MongoSettingStore.SETTING.terminalFontConfig()` 经 `FontManager.toFont` 得到 |
| `getClient()` | 获取客户端 | 返回 `client` |
| `flushPrompt()` | 刷新提示符 | 临时连接显示 `mongo connect@host`，否则用 `client.connectName()`；按 `isConnecting/isConnected` 追加 `(connecting)>`/`(connected)>`/`>`；最终 `prompt(str)` |
| `terminalName()` | 终端名称 | 返回 `TERMINAL_NAME` |
| `init(client, dbName)` | 初始化终端 | 保存 client/dbName；`FXUtil.runLater` 中禁用输入、输出欢迎语与版权行、刷新提示符，并按 `isTemporary()` 走 `initByTemporary`/`initByPermanent` |
| `getDbName()` / `setDbName(String)` | 获取/设置当前库 | `setDbName` 同时调用 `client.shellEngine().db(dbName)` |
| `isTemporary()` | 是否临时连接 | `client.iid() == null` |
| `outputPrompt()` | 输出提示符 | 客户端连接中时不输出，否则 `super` |
| `isConnected()` / `isConnecting()` / `isClosed()` | 连接状态判断 | 分别委托 `client.isConnected/isConnecting/isClosed` |
| `connect(String input)` | 执行连接命令 | `ShellMongoConnectUtil.parse(input)` 解析连接信息；禁用输入并 `ShellMongoConnectUtil.copyConnect` 到 `shellConnect()`，随后 `start()` |
| `initByTemporary()` | 临时连接初始化 | 输出 `connect` 命令用法提示与示例，启用输入并移动光标到尾部 |
| `initByPermanent()` | 常驻连接初始化 | 刷新提示符、追加提示行、启用输入、移动光标到尾部 |
| `start()` | 开始连接 | `TaskManager.startSync` 内初始化状态监听器并 `client.start()`；异常走 `onError(MongoExceptionParser.apply)`，finally 中 `enable()` |
| `initStatListener()` | 初始化连接状态监听器 | 懒创建监听器：状态变化时刷新提示符，并按 `CONNECTED/CLOSED/CONNECTING/INTERRUPTED/RECONNECTED/FAILED` 输出对应文案、启用输入；`FAILED` 时回填原始连接命令 |
| `enableInput()` | 启用输入 | 连接中直接返回；已连接或临时未连接时 `super.enableInput()` |
| `shellConnect()` | 获取连接信息 | `client.getShellConnect()` |
| `fontSizeIncr()` / `fontSizeDecr()` | 字号增减 | 调用 `super` 后 `saveFontSize()` |
| `saveFontSize()` | 保存字号 | 写入 `MongoSetting.terminalFontSize` 并 `MongoSettingStore.INSTANCE.replace` |
| `destroy()` | 销毁 | 解绑 `client.stateProperty()`、置空监听器后 `super.destroy()` |
| `eval(String input)` | 执行脚本 | 构造 `TerminalExecuteResult`，调用 `client.eval(dbName, input)` 设结果；异常写入 `setException` |
| `findHandler(String input)` | 查找命令处理器 | 先查 `TerminalManager.findHandler(TERMINAL_NAME, input)`；未命中则用匿名 `MongoTerminalCommandHandler`，执行时调 `terminal.eval` |
| `initNode()` | 初始化节点 | 依次设置 `keyHandler/helpHandler/mouseHandler/historyHandler/completeHandler` 为对应 `INSTANCE`，再 `super.initNode()` |

- 调用链：
  - `MongoTerminalTab → new MongoTerminalPane().init(client, dbName) → initByTemporary/initByPermanent`
  - `用户执行脚本 → findHandler → MongoTerminalCommandHandler.execute → MongoTerminalPane.eval → MongoClient.eval`
  - `client.start → 状态变化 → initStatListener → flushPrompt`

## MongoTerminalUtil

- 职责：Mongo 终端工具类，从命令行输入中提取路径参数。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getPath(String input)`（static） | 从输入中提取以 `/` 开头的路径 | 空格切分后跳过以 `-` 开头的选项，返回首个以 `/` 开头的片段，否则 `null` |

- 调用链：`终端脚本解析 → MongoTerminalUtil.getPath(input)`
