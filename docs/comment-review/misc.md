# misc 包代码审查（dto、exception、popups）

> 源码路径：
> - `src/main/java/cn/oyzh/easymongo/dto/`
> - `src/main/java/cn/oyzh/easymongo/exception/`
> - `src/main/java/cn/oyzh/easymongo/popups/`

## ShellMongoConnectInfo（dto）

- 职责：MongoDB shell 连接信息的传输对象（POJO），承载 shell 命令行解析出的连接参数。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `input` | `String` | 原始输入内容 |
| `host` | `String` | 地址，默认 `localhost` |
| `port` | `int` | 端口，默认 `2181` |
| `timeout` | `int` | 超时时间，单位毫秒，默认 `5000` |
| `readonly` | `boolean` | 是否只读模式 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getInput/setInput`、`getHost/setHost`、`getPort/setPort`、`getTimeout/setTimeout`、`isReadonly/setReadonly` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`ShellMongoConnectUtil → ShellMongoConnectInfo`（shell 连接信息解析与承载）

## MongoException（exception）

- 职责：MongoDB 业务异常，继承 `RuntimeException`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoException()` | 无参构造 | `super()` |
| `MongoException(String)` | 带消息构造 | `super(message)` |
| `MongoException(Exception)` | 带原因构造 | `super(ex)` |

- 调用链：`业务层 throw new MongoException(...) → MongoExceptionParser.apply`

## MongoExceptionParser（exception）

- 职责：MongoDB 异常信息解析器，实现 `Function<Throwable,String>`，用于 `MessageBox` 的异常信息展示。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoExceptionParser`（static final） | 单例实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `apply(Throwable)` | 将异常转换为可展示的消息 | 空返回 `null`；若为 `MongoException` 直接返回其 `getMessage()`；若为 `RuntimeException` 且有 `cause` 则下沉到 `cause`；对 `MongoException`/`UnsupportedOperationException`/`IllegalArgumentException` 返回其消息；其余情况先 `printStackTrace()` 再返回消息 |

- 调用链：`EasyMongoApp.init → MessageBox.registerExceptionParser(MongoExceptionParser.INSTANCE)`；`异常展示 → MongoExceptionParser.apply`

## MongoPageSettingPopupController（popups）

- 职责：「每页记录数」设置弹窗控制器。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `limit` | `NumberTextField`（`@FXML`） | 每页记录数输入框 |
| `setting` | `MongoSetting`（final） | 当前设置，来自 `MongoSettingStore.SETTING` |
| `settingStore` | `MongoSettingStore`（final） | 设置存储，来自 `MongoSettingStore.INSTANCE` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `apply()` | 应用每页记录数设置 | 读取 `limit.getIntValue()` → `setting.setRecordPageLimit(limit)` → `settingStore.update(setting)` → `submit(limit)` 返回参数 → `closeWindow()` |
| `close()` | 关闭弹窗 | `closeWindow()` |
| `bindListeners()` | 绑定监听（重写） | 仅调用 `super.bindListeners()` |
| `onWindowShowing(WindowEvent)` | 窗口显示时回调 | 将 `setting.getRecordPageLimit()` 回填到 `limit` |
| `onWindowHidden(WindowEvent)` | 窗口隐藏时回调 | 置空 `limit` 释放引用 |

- 调用链：`弹窗打开 → onWindowShowing（回填 limit） → apply → MongoSettingStore.update → submit/closeWindow`

## MongoRecordFilterPopupController（popups）

- 职责：数据过滤条件配置弹窗控制器。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `filterTable` | `FXTableView<MongoRecordFilter>`（`@FXML`） | 过滤条件表格 |
| `columnList` | `List<MongoColumn>` | 可选字段列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `apply()` | 应用过滤条件 | `submit(filterTable.getItems())` 返回过滤条件列表 → `closeWindow()` |
| `close()` | 关闭弹窗 | `closeWindow()` |
| `bindListeners()` | 绑定监听（重写） | 仅调用 `super.bindListeners()` |
| `onWindowShowing(WindowEvent)` | 窗口显示时回调 | 从属性取 `filters` 设置到表格，取 `columns` 存到 `columnList` |
| `addFilter()` | 添加过滤条件 | 新建 `MongoRecordFilter`，`setColumns(columnList)` 后 `filterTable.addItem(filter)` |
| `deleteFilter()` | 删除选中过滤条件 | 取 `getSelectedItem()`，非空则从表格移除 |

- 调用链：`弹窗打开 → onWindowShowing（载入 filters/columns） → addFilter/deleteFilter → apply → submit(过滤列表)`
