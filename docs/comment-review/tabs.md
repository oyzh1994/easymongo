# tabs 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/tabs/`

## MongoTab

- 职责：Mongo 标签页抽象基类，继承 `RichTab`，要求子类提供所属数据库树节点。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `dbItem()`（abstract） | 获取数据库树节点 | 由子类实现，返回 `MongoDatabaseTreeItem`，用于按数据库归属查找/关闭标签页 |

- 调用链：`MongoTabPane.getBaseTabs → tab1.dbItem() == dbItem`（按数据库匹配标签页）

## MongoTabPane

- 职责：Mongo 标签页面板，继承 `RichTabPane` 并实现 `FXEventListener`，通过订阅事件总线统一管理各类标签页（主页、集合记录、桶记录、查询、终端、函数设计、消息）的创建/选中/关闭。

- 字段：（无自有字段，状态来自父类 `RichTabPane`）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `flushHomeTab()` | 刷新主页标签 | 无标签时 `initHomeTab()`；标签数 > 1 时 `closeHomeTab()` |
| `getHomeTab()` | 获取主页标签 | 遍历 `getTabs()` 匹配 `MongoHomeTab` |
| `initHomeTab()` | 初始化主页标签 | 无主页标签时 `super.addTab(new MongoHomeTab())` |
| `closeHomeTab()` | 关闭主页标签 | 取 `getHomeTab()` 后 `super.removeTab` |
| `getMongoCollectionRecordTab(dbItem, tableName)` | 查找集合记录标签页 | 按 `dbItem` 引用相等与集合名匹配 `MongoCollectionRecordTab` |
| `onMongoCollectionOpen(MongoCollectionOpenEvent)` | 表打开事件（`@EventSubscribe`） | 无则新建 `MongoCollectionRecordTab` 并加入；`select(tab)` 后 `tab.init(event.data())` |
| `getBucketRecordTab(dbItem, bucketName)` | 查找桶记录标签页 | 按 `dbItem` + 桶名匹配 `MongoBucketRecordTab` |
| `onBucketOpen(MongoBucketOpenEvent)` | 桶打开事件 | 无则新建 `MongoBucketRecordTab`；`select` 后 `tab.init(event.data())` |
| `getMongoQueryMainTab(queryId)` | 查找查询主标签页 | 按 `queryId` 匹配 `MongoQueryMainTab` |
| `onMongoQueryAdd(MongoQueryAddEvent)` | 查询新增事件 | 新建 `MongoQueryMainTab` 并加入/选中，构造 `MongoQuery` 后 `tab.init(query, event.data())` |
| `onMongoQueryOpen(MongoQueryOpenEvent)` | 查询打开事件 | 按 `queryId` 查找，无则新建并 `tab.init(event.data(), event.getDbItem())`，最后选中 |
| `getTerminalTab(client, dbName)` | 查找终端标签页 | 按 `client` 引用相等与 `dbName` 匹配 `MongoTerminalTab` |
| `terminalOpen(MongoTerminalOpenEvent)` | 终端打开事件 | 无则新建 `MongoTerminalTab(client, dbName)`，有则 `flushGraphic()`；未选中则 `select` |
| `terminalClose(MongoTerminalCloseEvent)` | 终端关闭事件 | 找到对应终端标签页后 `closeTab()` |
| `getFunctionDesignTab(dbItem, functionName)` | 查找函数设计标签页 | 按 `dbItem` + 函数名匹配 `ShellMongoFunctionDesignTab` |
| `onFunctionRenamed(ShellMongoFunctionRenamedEvent)` | 函数重命名事件 | 关闭旧函数名对应的设计标签页 |
| `onFunctionDesign(ShellMongoFunctionDesignEvent)` | 函数设计事件 | 无则新建并 `tab.init(event.data(), event.getDbItem())`，最后选中 |
| `onFunctionDropped(ShellMongoFunctionDroppedEvent)` | 函数删除事件 | 关闭对应设计标签页 |
| `onCollectionRenamed(MongoCollectionRenamedEvent)` | 集合重命名事件 | 关闭新集合名对应的标签页 |
| `getBaseTabs(dbItem)` | 按数据库节点收集标签页 | 收集所有 `dbItem()` 等于指定节点的 `MongoTab` |
| `onDatabaseClosed(MongoDatabaseClosedEvent)` | 数据库关闭事件 | `removeTab(getBaseTabs(event.data()))` 批量关闭该库下标签页 |
| `getMongoTabs()` | 获取全部 Mongo 标签页 | 收集所有 `MongoTab` 实例 |
| `onConnectionClosed(MongoConnectionClosedEvent)` | 连接关闭事件 | `removeTab(getMongoTabs())` 关闭所有 Mongo 标签页 |
| `onQueryRenamed(MongoQueryRenamedEvent)` | 查询重命名事件 | 关闭对应查询主标签页 |
| `getMessageTab()` | 查找消息标签页 | 匹配 `ShellMessageTab` |
| `showMessage(ShellShowMessageEvent)` | 显示消息事件 | 无则新建 `ShellMessageTab` 加入，有则 `flushGraphic()`；未选中则选中 |

- 调用链：
  - `事件总线(MongoCollectionOpenEvent) → MongoTabPane.onMongoCollectionOpen → MongoCollectionRecordTab.init`
  - `事件总线(MongoConnectionClosedEvent) → MongoTabPane.onConnectionClosed → getMongoTabs → removeTab`
  - `MongoDatabaseClosedEvent → onDatabaseClosed → getBaseTabs → removeTab`
