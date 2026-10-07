# easymongo `event` 包代码审查文档

`cn.oyzh.easymongo.event` 及其子包定义了 easymongo 中各类 MongoDB 领域事件与事件发布工具，事件统一继承 `cn.oyzh.event.Event`，多数实现 `EventFormatter` 以提供可读的事件格式化文本。

覆盖类/接口/枚举数：29

---

## MongoEventUtil
- 职责：MongoDB 事件统一发布工具类，封装各类事件的构造与 `EventUtil.post` 投递。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| layout1() | 发送布局1事件 | `EventUtil.post(new Layout1Event())` |
| layout2() | 发送布局2事件 | `EventUtil.post(new Layout2Event())` |
| databaseClosed(MongoDatabaseTreeItem dbItem) | 发送数据库关闭事件 | 构造 `MongoDatabaseClosedEvent`，`event.data(dbItem)` 后 `EventUtil.post` |
| databaseAdded(MongoConnectTreeItem, MongoDatabase) | 发送数据库新增事件 | 构造 `MongoDatabaseAddedEvent`，设置 data 与 connectItem 后 post |
| databaseUpdated(MongoConnectTreeItem, MongoDatabase) | 发送数据库更新事件 | 构造 `MongoDatabaseUpdatedEvent` 后 post |
| databaseDropped(MongoDatabaseTreeItem) | 发送数据库删除事件 | 构造 `MongoDatabaseDroppedEvent` 后 post |
| queryAdd(MongoDatabaseTreeItem) | 发送查询新增事件 | 构造 `MongoQueryAddEvent` 后 post |
| queryAdded(MongoQuery, MongoDatabaseTreeItem) | 发送查询已新增事件 | 构造 `MongoQueryAddedEvent`，设置 data 与 dbItem 后 post |
| queryDeleted(MongoQueryTreeItem) | 发送查询删除事件 | 构造 `MongoQueryDeletedEvent` 后 post |
| queryOpen(MongoQuery, MongoDatabaseTreeItem) | 发送查询打开事件 | 构造 `MongoQueryOpenEvent` 后 post |
| queryRenamed(String queryId, String queryName, String newQueryName, MongoDatabaseTreeItem) | 发送查询重命名事件 | 设置 data(queryId)、queryName、newQueryName、dbItem 后 post |
| connectUpdated(MongoConnect) | 发送连接已修改事件 | 构造 `MongoConnectUpdatedEvent` 后 post |
| addConnect() | 发送新增连接事件 | `EventUtil.post(new MongoAddConnectEvent())` |
| addGroup() | 发送新增分组事件 | `EventUtil.post(new MongoAddGroupEvent())` |
| changelog() | 发送更新日志事件 | `EventUtil.post(new ChangelogEvent())` |
| connectAdded(MongoConnect) | 发送连接已新增事件 | 构造 `MongoConnectAddedEvent` 后 post |
| connectDeleted(MongoConnect) | 发送连接已删除事件 | 构造 `MongoConnectDeletedEvent` 后 post |
| treeItemChanged(TreeItem<?>) | 发送树节点变更事件 | 构造 `MongoTreeItemChangedEvent` 后 post |
| collectionDropped(MongoCollectionTreeItem, MongoDatabaseTreeItem) | 发送集合删除事件 | 构造 `MongoCollectionDroppedEvent` 后 post |
| collectionOpen(MongoCollectionTreeItem, MongoDatabaseTreeItem) | 发送集合打开事件 | 构造 `MongoCollectionOpenEvent` 后 post |
| collectionRenamed(String, String, MongoDatabaseTreeItem) | 发送集合重命名事件 | 构造 `MongoCollectionRenamedEvent` 后 post |
| bucketDropped(MongoBucketTreeItem, MongoDatabaseTreeItem) | 发送桶删除事件 | 构造 `MongoBucketDroppedEvent` 后 post |
| bucketOpen(MongoBucketTreeItem, MongoDatabaseTreeItem) | 发送桶打开事件 | 构造 `MongoBucketOpenEvent` 后 post |
| terminalOpen(MongoClient, String dbName) | 发送终端打开事件 | 构造 `MongoTerminalOpenEvent`，设置 data 与 dbName 后 post |
| dropFunction(ShellMongoFunctionTreeItem) | 发送函数删除事件 | 构造 `ShellMongoFunctionDroppedEvent` 后 `EventUtil.postSync`（同步投递） |
| designFunction(MongoFunction, MongoDatabaseTreeItem) | 发送函数设计事件 | 构造 `ShellMongoFunctionDesignEvent` 后 post |
| functionRenamed(String, String, MongoDatabaseTreeItem) | 发送函数重命名事件 | 构造 `ShellMongoFunctionRenamedEvent` 后 post |
| connectionClosed(MongoClient) | 发送连接关闭事件 | 构造 `MongoConnectionClosedEvent` 后 post |
| connectionConnected(MongoClient) | 发送连接成功事件 | 构造 `MongoConnectionConnectedEvent` 后 post |
| showMessage() | 显示消息页面 | `EventUtil.post(new ShellShowMessageEvent())` |

- 调用链：
  - `业务代码 → MongoEventUtil.xxx() → new MongoXxxEvent() → EventUtil.post(event)`
  - `MongoEventUtil.dropFunction() → EventUtil.postSync(ShellMongoFunctionDroppedEvent)`

---

## MongoBucketDroppedEvent
- 职责：MongoDB 桶删除事件，携带被删除的桶树节点与所属数据库树节点。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| bucketName() | 获取桶名称 | 委托 `data().bucketName()` |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] dropped", I18nHelper.bucket(), bucketName())` |

- 调用链：`MongoEventUtil.bucketDropped() → new MongoBucketDroppedEvent → EventUtil.post`

---

## MongoBucketOpenEvent
- 职责：MongoDB 桶打开事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| bucketName() | 获取桶名称 | 委托 `data().bucketName()` |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |

- 调用链：`MongoEventUtil.bucketOpen() → new MongoBucketOpenEvent → EventUtil.post`

---

## MongoCollectionDroppedEvent
- 职责：MongoDB 集合删除事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| collectionName() | 获取集合名称 | 委托 `data().collectionName()` |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] dropped", I18nHelper.collection(), collectionName())` |

- 调用链：`MongoEventUtil.collectionDropped() → new MongoCollectionDroppedEvent → EventUtil.post`

---

## MongoCollectionOpenEvent
- 职责：MongoDB 集合打开事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| collectionName() | 获取集合名称 | 委托 `data().collectionName()` |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |

- 调用链：`MongoEventUtil.collectionOpen() → new MongoCollectionOpenEvent → EventUtil.post`

---

## MongoCollectionRenamedEvent
- 职责：MongoDB 集合重命名事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |
| newCollectionName | String | 新集合名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getNewCollectionName() / setNewCollectionName(...) | 读写新集合名称 | — |
| tableName() | 获取原集合名称 | 返回 `this.data()`（泛型为 String） |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] renamed, new name:%s", ...)` |

- 调用链：`MongoEventUtil.collectionRenamed() → new MongoCollectionRenamedEvent → EventUtil.post`

---

## MongoAddConnectEvent
- 职责：MongoDB 新增连接事件（空事件载体）。

- 字段：无

- 方法：无

- 调用链：`MongoEventUtil.addConnect() → EventUtil.post(new MongoAddConnectEvent())`

---

## MongoConnectAddedEvent
- 职责：MongoDB 连接已新增事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] added", I18nHelper.connect(), data().getName())` |

- 调用链：`MongoEventUtil.connectAdded() → new MongoConnectAddedEvent → EventUtil.post`

---

## MongoConnectDeletedEvent
- 职责：MongoDB 连接已删除事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] deleted", I18nHelper.connect(), data().getName())` |

- 调用链：`MongoEventUtil.connectDeleted() → new MongoConnectDeletedEvent → EventUtil.post`

---

## MongoConnectUpdatedEvent
- 职责：MongoDB 连接已更新事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s updated] ", I18nHelper.connect(), data().getName())` |

- 调用链：`MongoEventUtil.connectUpdated() → new MongoConnectUpdatedEvent → EventUtil.post`

---

## MongoConnectionClosedEvent
- 职责：MongoDB 连接关闭事件，携带 `MongoClient`。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] closed", I18nHelper.connect(), data().connectName())` |
| shellConnect() | 获取 Shell 连接信息 | 委托 `data().getShellConnect()` |

- 调用链：`MongoEventUtil.connectionClosed() → new MongoConnectionClosedEvent → EventUtil.post`

---

## MongoConnectionConnectedEvent
- 职责：MongoDB 连接成功事件，携带 `MongoClient`。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] connected", I18nHelper.connect(), data().connectName())` |
| shellConnect() | 获取 Shell 连接信息 | 委托 `data().getShellConnect()` |

- 调用链：`MongoEventUtil.connectionConnected() → new MongoConnectionConnectedEvent → EventUtil.post`

---

## MongoDatabaseAddedEvent
- 职责：MongoDB 数据库新增事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| connectItem | MongoConnectTreeItem | 连接树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] added", I18nHelper.database(), data().getName())` |
| getConnectItem() / setConnectItem(...) | 读写连接树节点 | — |

- 调用链：`MongoEventUtil.databaseAdded() → new MongoDatabaseAddedEvent → EventUtil.post`

---

## MongoDatabaseClosedEvent
- 职责：MongoDB 数据库关闭事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] closed", I18nHelper.database(), data().value())` |

- 调用链：`MongoEventUtil.databaseClosed() → new MongoDatabaseClosedEvent → EventUtil.post`

---

## MongoDatabaseDroppedEvent
- 职责：MongoDB 数据库删除事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本（未实现接口，普通方法） | `String.format("[%s:%s] deleted", I18nHelper.database(), data().dbName())` |

- 调用链：`MongoEventUtil.databaseDropped() → new MongoDatabaseDroppedEvent → EventUtil.post`

---

## MongoDatabaseUpdatedEvent
- 职责：MongoDB 数据库更新事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| connectItem | MongoConnectTreeItem | 连接树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] updated", I18nHelper.database(), data().getName())` |
| getConnectItem() / setConnectItem(...) | 读写连接树节点 | — |

- 调用链：`MongoEventUtil.databaseUpdated() → new MongoDatabaseUpdatedEvent → EventUtil.post`

---

## ShellMongoFunctionDesignEvent
- 职责：MongoDB 函数设计（编辑）事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| functionName() | 获取函数名称 | 委托 `data().getName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |

- 调用链：`MongoEventUtil.designFunction() → new ShellMongoFunctionDesignEvent → EventUtil.post`

---

## ShellMongoFunctionDroppedEvent
- 职责：MongoDB 函数删除事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| functionName() | 获取函数名称 | 委托 `data().functionName()` |
| getDbItem() | 获取数据库树节点 | 委托 `data().dbItem()` |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] dropped", I18nHelper.function(), functionName())` |

- 调用链：`MongoEventUtil.dropFunction() → new ShellMongoFunctionDroppedEvent → EventUtil.postSync`

---

## ShellMongoFunctionRenamedEvent
- 职责：MongoDB 函数重命名事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |
| newFunctionName | String | 新函数名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getNewFunctionName() / setNewFunctionName(...) | 读写新函数名称 | — |
| functionName() | 获取原函数名称 | 返回 `this.data()` |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] renamed, new name:%s", ...)` |

- 调用链：`MongoEventUtil.functionRenamed() → new ShellMongoFunctionRenamedEvent → EventUtil.post`

---

## MongoAddGroupEvent
- 职责：MongoDB 新增分组事件（空事件载体）。

- 字段：无

- 方法：无

- 调用链：`MongoEventUtil.addGroup() → EventUtil.post(new MongoAddGroupEvent())`

---

## MongoQueryAddEvent
- 职责：MongoDB 查询新增事件，携带数据库树节点。

- 字段：无

- 方法：无

- 调用链：`MongoEventUtil.queryAdd() → new MongoQueryAddEvent → EventUtil.post`

---

## MongoQueryAddedEvent
- 职责：MongoDB 查询已新增事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] added", I18nHelper.query(), data().getName())` |

- 调用链：`MongoEventUtil.queryAdded() → new MongoQueryAddedEvent → EventUtil.post`

---

## MongoQueryDeletedEvent
- 职责：MongoDB 查询删除事件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| queryId() | 获取查询标识 | 委托 `data().value().getUid()` |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] deleted", I18nHelper.query(), data().queryName())` |

- 调用链：`MongoEventUtil.queryDeleted() → new MongoQueryDeletedEvent → EventUtil.post`

---

## MongoQueryOpenEvent
- 职责：MongoDB 查询打开事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| queryId() | 获取查询标识 | 委托 `data().getUid()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |

- 调用链：`MongoEventUtil.queryOpen() → new MongoQueryOpenEvent → EventUtil.post`

---

## MongoQueryRenamedEvent
- 职责：MongoDB 查询重命名事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbItem | MongoDatabaseTreeItem | 数据库树节点 |
| queryName | String | 原查询名称 |
| newQueryName | String | 新查询名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getQueryName() / setQueryName(...) | 读写原查询名称 | — |
| getNewQueryName() / setNewQueryName(...) | 读写新查询名称 | — |
| dbName() | 获取数据库名称 | `dbItem.dbName()` |
| getDbItem() / setDbItem(...) | 读写数据库树节点 | — |
| eventFormat() | 事件格式化文本 | `String.format("[%s:%s] renamed, new name:%s", ...)` |

- 调用链：`MongoEventUtil.queryRenamed() → new MongoQueryRenamedEvent → EventUtil.post`

---

## MongoTerminalCloseEvent
- 职责：MongoDB 终端关闭事件，携带 `MongoClient`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getDbName() / setDbName(...) | 读写数据库名称 | — |

- 调用链：`业务代码 → new MongoTerminalCloseEvent → EventUtil.post`

---

## MongoTerminalOpenEvent
- 职责：MongoDB 终端打开事件，携带 `MongoClient`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getDbName() / setDbName(...) | 读写数据库名称 | — |

- 调用链：`MongoEventUtil.terminalOpen() → new MongoTerminalOpenEvent → EventUtil.post`

---

## MongoTreeItemChangedEvent
- 职责：MongoDB 树节点变更事件，携带 `TreeItem<?>`。

- 字段：无

- 方法：无

- 调用链：`MongoEventUtil.treeItemChanged() → new MongoTreeItemChangedEvent → EventUtil.post`

---

## ShellShowMessageEvent
- 职责：显示消息页面事件（空事件载体）。

- 字段：无

- 方法：无

- 调用链：`MongoEventUtil.showMessage() → EventUtil.post(new ShellShowMessageEvent())`
