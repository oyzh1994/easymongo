# easymongo `trees` 包代码审查文档

`cn.oyzh.easymongo.trees` 及其子包实现了 easymongo 左侧数据库树（连接 / 分组 / 数据库 / 集合 / GridFS 桶 / 函数 / 查询 / 终端）的树节点与树视图，节点类基于 `RichTreeItem`，展示值类基于 `RichTreeItemValue`。

覆盖类/接口/枚举数：30

---

## MongoConnectManager
- 职责：连接管理接口，定义树中连接的增删查及"仅已连接节点"的默认实现。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| addConnect(MongoConnect) | 添加连接（抽象） | — |
| addConnects(List<MongoConnect>) | 批量添加连接 | 遍历调用 `addConnect` |
| addConnectItem(MongoConnectTreeItem) | 添加连接树节点（抽象） | — |
| addConnectItems(List<MongoConnectTreeItem>) | 批量添加连接树节点（抽象） | — |
| delConnectItem(MongoConnectTreeItem) | 删除连接树节点（抽象） | — |
| getConnectItems() | 获取全部连接树节点（抽象） | — |
| getConnectedItems() | 获取已连接的连接节点 | `getConnectItems().parallelStream().filter(MongoConnectTreeItem::isConnected)` |

- 调用链：`MongoRootTreeItem.getConnectedItems() ← MongoTreeView.closeConnects()`
- 调用链：`MongoGroupTreeItem/MongoRootTreeItem → 实现 addConnect/delConnectItem → MongoConnectStore`

---

## MongoTreeItem
- 职责：easymongo 基础树节点抽象类，统一泛型值类型并强制 `getTreeView` 返回 `MongoTreeView`。

- 字段：无（继承 `RichTreeItem`）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoTreeItem(RichTreeView treeView) | 构造树节点 | 调用 `super(treeView)` |
| getTreeView() | 获取树视图 | 将 `super.getTreeView()` 强转为 `MongoTreeView` |

- 调用链：`各具体 TreeItem → extends MongoTreeItem<V>`（所有节点均继承自此）

---

## MongoTreeItemFilter
- 职责：树节点过滤器，当前仅保留"不可过滤节点直接通过"逻辑。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| onlyCollect | boolean | 是否仅看收藏键 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| test(RichTreeItem<?> item) | 过滤判定 | 非 `isFilterable()` 节点直接返回 true，否则返回 true（搜索逻辑已注释） |
| isOnlyCollect() / setOnlyCollect(...) | 读写 onlyCollect | — |

- 调用链：`MongoTreeView.getItemFilter() → new MongoTreeItemFilter → RichTreeItem.doFilter`

---

## MongoTreeView
- 职责：easymongo 主数据库树视图，负责根节点初始化、关闭连接以及响应连接相关事件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| searching | volatile boolean | 搜索中标志位 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getItemFilter() | 获取/初始化过滤器 | 惰性创建 `MongoTreeItemFilter` |
| MongoTreeView() | 构造树视图 | 设置拖拽标记、单选模式、CellFactory，`setRoot(new MongoRootTreeItem(this))` 并展开根节点 |
| root() | 获取根节点 | 强转为 `MongoRootTreeItem` |
| closeConnects() | 关闭所有连接 | 遍历 `root().getConnectedItems()`，`ThreadUtil.start(item::closeConnect)` |
| onInfoUpdate(MongoConnectUpdatedEvent) | 连接修改事件 | 遍历根节点及分组子节点，匹配 `value()==event.data()` 后刷新值 |
| addConnect(MongoAddConnectEvent) | 添加连接事件 | `StageManager.showStage(MongoConnectAddController.class, window())` |
| addGroup(MongoAddGroupEvent) | 添加分组事件 | `root().addGroup()` |
| infoAdded(MongoConnectAddedEvent) | 连接新增事件 | `root().addConnect(event.data())` |
| infoUpdated(MongoConnectUpdatedEvent) | 连接变更事件 | `root().infoUpdate(event.data())` |

- 调用链：`应用启动 → new MongoTreeView() → MongoRootTreeItem.initChildes()`
- 调用链：`MongoEventUtil.connectAdded → MongoConnectAddedEvent → MongoTreeView.infoAdded → MongoRootTreeItem.addConnect`

---

## MongoBucketTreeItem
- 职责：GridFS 桶树节点，提供桶记录的分页查询、上传下载、删除及脚本执行。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoBucket | 当前桶对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoBucketTreeItem(MongoBucket, RichTreeView) | 构造桶节点 | 设置 value 与 `MongoBucketTreeItemValue` |
| parent() | 获取父节点 | 强转为 `MongoBucketsTreeItem` |
| client() | 获取 Mongo 客户端 | `parent().client()` |
| dbName() / bucketName() / info() / infoName() | 获取数据库名/桶名/连接信息/连接名 | 多委托至父节点或 value |
| getMenuItems() | 构建右键菜单 | `MenuItemHelper.openBucket/clearBucket/deleteBucket` |
| clearBucket() | 清空桶 | 确认后 `dbItem().clearBucket(name)` 并 `parent().reloadChild()` |
| delete() | 删除桶 | 确认后 `dbItem().dropBucket(name)`，`MongoEventUtil.bucketDropped(this, dbItem())`，`remove()` |
| dbItem() | 获取所属数据库节点 | `parent().parent()` |
| onPrimaryDoubleClick() | 双击打开 | `MongoEventUtil.bucketOpen(this, dbItem())` |
| loadChild() / reloadChild() | 子节点加载/重载 | reload 清空后重新 load |
| value() | 获取桶对象 | — |
| bucketColumns() | 获取字段集 | `client().bucketColumns()` |
| recordPage(pageNo, limit, filters, columns) | 分页查询记录 | 构造 `MongoSelectRecordParam`，调用 `selectBucketRecords`/`selectBucketRecordCount` 组装 `Paging` |
| uploadRecord(File) | 上传记录 | `client().uploadBucketRecord(...)` |
| selectRecord(Object) | 查询记录 | `client().selectBucketRecord(...)` |
| downloadRecord(Object, String) | 下载记录 | `client().downloadBucketRecord(...)` |
| deleteRecord(Object)/deleteRecord(MongoRecord) | 删除记录 | `client().deleteBucketRecord(...)` |
| updateRecord(MongoRecord) | 更新记录 | `client().updateBucketRecord(...)` |
| eval(String) | 执行脚本 | `client().eval(dbName, script)` |

- 调用链：`MongoBucketsTreeItem.loadChild → new MongoBucketTreeItem → UI 展示`
- 调用链：`用户删除桶 → MongoBucketTreeItem.delete → MongoEventUtil.bucketDropped`

---

## MongoBucketTreeItemValue
- 职责：桶树节点的展示值（图标/名称）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoBucketTreeItemValue(MongoBucketTreeItem) | 构造值 | `super(item)` |
| item() | 获取所属节点 | 强转 `MongoBucketTreeItem` |
| graphic() | 获取图标 | 惰性创建 `BucketSVGGlyph` |
| name() | 显示名称 | `item().bucketName()` |

- 调用链：`MongoBucketTreeItem → setValue(new MongoBucketTreeItemValue(this))`

---

## MongoBucketsTreeItem
- 职责：GridFS 桶类型节点（"GridFS" 分组），负责加载/同步桶列表与新增桶。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| bucketsSize | Integer | 桶数量缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoBucketsTreeItem(RichTreeView) | 构造桶类型节点 | 设置 filterable、值对象，监听子节点变化清空 `bucketsSize` |
| parent() | 父节点 | 强转 `MongoDatabaseTreeItem` |
| getMenuItems() | 右键菜单 | `MenuItemHelper.addBucket/reloadData` |
| addBucket() | 新增桶 | 提示名称后 `client().createBucket(dbName, name)` 并 reload |
| itemVisible() | 是否可见 | `isVisible()` |
| loadChild() | 加载桶列表 | Task 内 `client().listBuckets(dbName)`，空则整体 setChild，否则按 compare 做增删更新，成功后 refresh |
| reloadChild() | 重载 | 清空 + `loadChild` |
| dbName()/client()/info()/infoName() | 委托父节点 | — |
| onPrimaryDoubleClick() | 双击 | 未加载则加载，否则 super |
| bucketsSize()/getBucketsSize() | 桶数量 | `parent().listBucketNames().size()` 并缓存 |

- 调用链：`MongoDatabaseTreeItem.loadChild → new MongoBucketsTreeItem → loadChild → MongoClient.listBuckets`
- 调用链：`MongoBucketsTreeItem.addBucket → MongoClient.createBucket → reloadChild`

---

## MongoBucketsTreeItemValue
- 职责：桶类型节点展示值，名称为 "GridFS"，附带桶数量后缀。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoBucketsTreeItemValue(MongoBucketsTreeItem) | 构造值 | 设置 richMode |
| item() | 获取节点 | 强转 |
| name() | 名称 | 返回常量 "GridFS" |
| graphic() | 图标 | 惰性创建 `BucketSVGGlyph` 并 `disableTheme` |
| extra() | 附加文本 | `getBucketsSize()` 非空时返回 `" (size)"` |
| extraColor() | 附加颜色 | `#228B22` |

- 调用链：`MongoBucketsTreeItem → setValue(new MongoBucketsTreeItemValue(this))`

---

## MongoCollectionTreeItem
- 职责：集合树节点，提供集合记录的增删改查、重命名、清空、转储/导出等操作。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoCollection | 当前集合对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCollectionTreeItem(MongoCollection, RichTreeView) | 构造集合节点 | 设置 value 与值对象 |
| parent() | 父节点 | 强转 `MongoCollectionsTreeItem` |
| client()/dbName()/collectionName()/info()/infoName() | 委托父节点/值 | — |
| getMenuItems() | 右键菜单 | open/rename/clear/delete + separator + dump/export |
| dump() | 转储 | `MongoViewFactory.dumpData(client, dbName, collectionName, 1)` |
| export() | 导出 | `MongoViewFactory.exportData(...)` |
| clearCollection() | 清空集合 | 确认后 `dbItem().clearCollection(name)` + reload |
| delete() | 删除集合 | 确认后 `dbItem().dropCollection(name)`、`MongoEventUtil.collectionDropped`、`remove()` |
| rename() | 重命名集合 | 校验名称后 `dbItem().renameCollection`、更新 value、refresh、`MongoEventUtil.collectionRenamed` |
| dbItem() | 所属数据库节点 | `parent().parent()` |
| onPrimaryDoubleClick() | 双击打开 | `MongoEventUtil.collectionOpen(this, dbItem())` |
| reloadChild() | 重载子节点 | clearChild + setLoaded(false) + loadChild |
| value() | 获取集合对象 | — |
| recordPage(pageNo, limit, filters, columns) | 分页查询 | `selectCollectionRecords`/`selectCollectionRecordCount` 组装 `Paging` |
| insertRecord(MongoRecord) | 插入记录 | `dbItem().insertCollectionRecord(record)` |
| deleteRecord(MongoRecord) | 删除记录 | `dbItem().deleteCollectionRecord(record)` |
| updateRecord(MongoRecord) | 更新记录 | `dbItem().updateCollectionRecord(record)` |
| eval(String) | 执行脚本 | `dbItem().eval(script)` |
| selectCollectionRecord(Object) | 查询记录 | `dbItem().selectCollectionRecord(name, id)` |

- 调用链：`MongoCollectionsTreeItem.loadChild → new MongoCollectionTreeItem`
- 调用链：`用户重命名 → MongoCollectionTreeItem.rename → MongoDatabaseTreeItem.renameCollection → MongoEventUtil.collectionRenamed`

---

## MongoCollectionTreeItemValue
- 职责：集合树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCollectionTreeItemValue(MongoCollectionTreeItem) | 构造值 | `super(item)` |
| item() | 获取节点 | 强转 |
| graphic() | 图标 | 惰性创建 `TableSVGGlyph` |
| name() | 名称 | `item().collectionName()` |

- 调用链：`MongoCollectionTreeItem → setValue(new MongoCollectionTreeItemValue(this))`

---

## MongoCollectionsTreeItem
- 职责：集合类型节点（"集合"分组），负责同步集合列表、新增集合、导入导出数据。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| collectionsSize | Integer | 集合数量缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCollectionsTreeItem(RichTreeView) | 构造集合类型节点 | 设置 filterable、值对象、子节点变化监听 |
| parent() | 父节点 | 强转 `MongoDatabaseTreeItem` |
| getMenuItems() | 右键菜单 | add/reload/exportData/importData |
| exportData()/importData() | 导出/导入数据 | `MongoViewFactory.exportData/importData` |
| addCollection() | 新增集合 | 提示名称后 `client().createCollection(collection)` 并 reload |
| itemVisible() | 可见性 | `isVisible()` |
| loadChild() | 加载集合列表 | `client().listCollections` 做增删更新，成功后 refresh |
| reloadChild() | 重载 | clearChild + loadChild |
| dbName()/client()/info()/infoName() | 委托父节点 | — |
| onPrimaryDoubleClick() | 双击 | 未加载则加载 |
| collectionsSize()/getCollectionsSize() | 集合数量 | `parent().listCollectionNames().size()` 并缓存 |

- 调用链：`MongoDatabaseTreeItem.loadChild → new MongoCollectionsTreeItem → loadChild → MongoClient.listCollections`

---

## MongoCollectionsTreeItemValue
- 职责：集合类型节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCollectionsTreeItemValue(MongoCollectionsTreeItem) | 构造值 | 设置 richMode |
| item() | 获取节点 | 强转 |
| name() | 名称 | `I18nHelper.collections()` |
| graphic() | 图标 | 惰性创建 `TableSVGGlyph` 并 disableTheme |
| extra() | 附加文本 | `getCollectionsSize()` 非空时返回 `" (size)"` |
| extraColor() | 附加颜色 | `#228B22` |

- 调用链：`MongoCollectionsTreeItem → setValue(new MongoCollectionsTreeItemValue(this))`

---

## MongoConnectTreeItem
- 职责：连接树节点，管理 Mongo 客户端的连接/断开、数据库节点加载、连接信息的增删改及拖拽。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoConnect | 连接信息 |
| client | MongoClient | Mongo 客户端 |
| canceled | boolean | 已取消操作标志位 |
| connectStore | MongoConnectStore | 连接信息存储（单例） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoConnectTreeItem(MongoConnect, MongoTreeView) | 构造连接节点 | 调用 `value(value)` 初始化 client |
| reloadChild() | 重载 | clearChild + loadChild |
| getMenuItems() | 右键菜单 | 按连接状态（连接中/已连接/未连接）构建不同菜单项 |
| addDatabase() | 新增数据库（@FXML） | `MongoViewFactory.databaseAdd(this)` 取 databaseName 后 addDatabase |
| addDatabase(String) | 新增数据库节点 | `client.database(name)` 后 addChild `MongoDatabaseTreeItem` |
| cancelConnect() | 取消连接 | 置 canceled 并 `client.close()`、stopWaiting |
| connect() | 连接 | Task 内 `client.start()`，失败提示并 closeConnect(false)，成功 loadChild |
| closeConnect()/closeConnect(boolean) | 关闭连接 | `client.close()` + clearChild，可按需等待动画 |
| editConnect() | 编辑连接 | 已连接则先确认关闭，再弹 `MongoConnectUpdateController` |
| repeatConnect() | 复制连接 | 复制 value、改名、`connectStore.insert` + `connectManager().addConnect` |
| delete() | 删除连接 | 确认后 `closeConnect(false)`，`delConnectItem`，`MongoEventUtil.connectDeleted` |
| rename() | 重命名连接 | 校验后 `connectStore.update` 并重设值对象 |
| value(MongoConnect) | 设置连接信息 | 记录 value、`new MongoClient(value)`、重设值对象 |
| value() | 获取连接信息 | — |
| isConnected()/isConnecting() | 连接状态 | 委托 `client.isConnected()/isConnecting()` |
| connectManager() | 获取连接管理器 | 父节点实现 `MongoConnectManager` 时返回 |
| allowDrag() | 允许拖拽 | true |
| loadChild() | 加载数据库列表 | `client.listDatabases()` → `MongoDatabaseTreeItem` 列表 |
| onPrimaryDoubleClick() | 双击 | 未连接则 connect |
| existDatabase/createDatabase/alterDatabase/dropDatabase | 数据库操作 | 委托 client |
| type() | 连接类型 | `value.getType()` |
| getClient() | 获取客户端 | — |

- 调用链：`MongoTreeView.addConnect → MongoConnectAddController → MongoConnectStore.insert → MongoTreeView.infoAdded → MongoRootTreeItem.addConnect`
- 调用链：`MongoConnectTreeItem.connect → MongoClient.start → loadChild → MongoDatabaseTreeItem`

---

## MongoConnectTreeItemValue
- 职责：连接树节点展示值，图标颜色随连接状态变化。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoConnectTreeItemValue(MongoConnectTreeItem) | 构造值 | `super(item)` |
| item() | 获取节点 | 强转 |
| name() | 名称 | `item().value().getName()` |
| graphic() | 图标 | 惰性创建 `MongodbSVGGlyph` 并 disableTheme |
| graphicColor() | 图标颜色 | 子节点非空时返回 `Color.GREEN` |

- 调用链：`MongoConnectTreeItem → setValue(new MongoConnectTreeItemValue(this))`

---

## MongoDatabaseTreeItem
- 职责：数据库树节点，聚合集合/桶/函数/查询/终端类型子节点，并转发大量数据库级操作。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoDatabase | 当前数据库对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| value() | 获取数据库对象 | — |
| MongoDatabaseTreeItem(MongoDatabase, RichTreeView) | 构造数据库节点 | setSortable(false)、setFilterable(true)、设置值对象 |
| parent() | 父节点 | 强转 `MongoConnectTreeItem` |
| dbName()/userName() | 数据库名/用户名 | `value.getName()` / `info().getUser()` |
| getMenuItems() | 右键菜单 | closeDB/delete/dump/runScriptFile（编辑项已注释） |
| dump() | 转储 | `MongoViewFactory.dumpData(client, dbName, null, 1)` |
| runScriptFile() | 运行脚本文件 | `MongoViewFactory.runScriptFile(client, dbName)` |
| delete() | 删除数据库 | Task 内 `parent().dropDatabase`，`MongoEventUtil.databaseDropped`，remove |
| closeDB() | 关闭数据库 | clearChild、collapse、setLoaded(false)、`MongoEventUtil.databaseClosed` |
| loadChild() | 加载类型子节点 | 构建并添加 Collections/Buckets/Functions/Queries/Terminal 类型节点 |
| getQueryTypeChild()/getFunctionTypeChild() | 获取查询/函数类型子节点 | 遍历 richChildren 匹配类型 |
| client()/info()/infoName()/connectName()/shellConnect() | 委托父节点 | — |
| onPrimaryDoubleClick() | 双击 | 未加载则 loadChild |
| itemVisible() | 可见性 | `isVisible()` |
| dropCollection/clearCollection/dropBucket/clearBucket | 集合/桶操作 | 委托 client |
| executeSingleScript(String) | 执行单条脚本 | `client().executeSingleScript(dbName, script)` |
| executeScript(String) | 执行脚本 | `client().executeScript(dbName, script)` |
| deleteCollectionRecord/updateCollectionRecord/insertCollectionRecord/selectCollectionRecord | 集合记录操作 | 委托 client |
| dropFunction/renameFunction/selectFunction/createFunction/alertFunction | 函数操作 | 委托 client |
| renameCollection(oldName, newName) | 重命名集合 | `client().renameCollection(...)` |
| listCollectionNames()/listBucketNames() | 名称列表 | 委托 client |
| eval(String) | 执行脚本 | `client().eval(dbName, script)` |

- 调用链：`MongoConnectTreeItem.loadChild → new MongoDatabaseTreeItem → onPrimaryDoubleClick → loadChild（类型节点）`
- 调用链：`MongoDatabaseTreeItem.delete → parent().dropDatabase → MongoEventUtil.databaseDropped`

---

## MongoDatabaseTreeItemValue
- 职责：数据库树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoDatabaseTreeItemValue(MongoDatabaseTreeItem) | 构造值 | `super(item)` |
| item() | 获取节点 | 强转 |
| name() | 名称 | `item().dbName()` |
| graphic() | 图标 | 惰性创建 `DatabaseSVGGlyph` 并 disableTheme |
| graphicColor() | 图标颜色 | 子节点非空时 `Color.GREEN` |

- 调用链：`MongoDatabaseTreeItem → setValue(new MongoDatabaseTreeItemValue(this))`

---

## ShellMongoFunctionTreeItem
- 职责：函数树节点，提供查看、克隆、删除、重命名函数及加载函数代码。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoFunction | 当前函数对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoFunctionTreeItem(MongoFunction, RichTreeView) | 构造函数节点 | setFilterable(true)、设置值对象 |
| parent() | 父节点 | 强转 `ShellMongoFunctionsTreeItem` |
| client()/info()/infoName() | 委托父节点 | — |
| getMenuItems() | 右键菜单 | design/rename/delete + separator + clone/functionInfo |
| functionInfo() | 查看函数信息 | 空实现 |
| cloneFunction() | 克隆函数 | 生成新名、复制 code、`dbItem().createFunction` 并 `getFunctionTypeChild().addFunction` |
| delete() | 删除函数 | 确认后 `MongoEventUtil.dropFunction(this)`、`dbItem().dropFunction`、remove |
| dbItem() | 所属数据库节点 | `parent().parent()` |
| dbName() | 数据库名称 | `parent().dbName()` |
| onPrimaryDoubleClick() | 双击设计 | `MongoEventUtil.designFunction(value, dbItem())` |
| functionName() | 函数名称 | `value.getName()` |
| reloadChild() | 重载 | clearChild + loadChild |
| loadChild() | 加载函数内容 | `client().selectFunction(dbName, name)` 后 `value.copy(function)` |
| onPrimarySingleClick() | 单击 | super |
| value() | 获取函数对象 | — |
| rename() | 重命名函数 | 校验后 `dbItem().renameFunction`、refresh、`MongoEventUtil.functionRenamed` |

- 调用链：`ShellMongoFunctionsTreeItem.loadChild → new ShellMongoFunctionTreeItem`
- 调用链：`ShellMongoFunctionTreeItem.delete → MongoEventUtil.dropFunction → MongoDatabaseTreeItem.dropFunction`

---

## ShellMongoFunctionTreeItemValue
- 职责：函数树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoFunctionTreeItemValue(ShellMongoFunctionTreeItem) | 构造值 | 设置 richMode |
| item() | 获取节点 | 强转 |
| graphic() | 图标 | 惰性创建 `FunctionSVGGlyph` |
| name() | 名称 | `item().functionName()` |

- 调用链：`ShellMongoFunctionTreeItem → setValue(new ShellMongoFunctionTreeItemValue(this))`

---

## ShellMongoFunctionsTreeItem
- 职责：函数类型节点（"函数"分组），负责同步函数列表与新增/添加函数节点。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| functionSize | Integer | 函数数量缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoFunctionsTreeItem(RichTreeView) | 构造函数类型节点 | setFilterable(true)、值对象、子节点变化监听 |
| parent() | 父节点 | 强转 `MongoDatabaseTreeItem` |
| getMenuItems() | 右键菜单 | add/reload |
| add() | 新增函数 | `new MongoFunction` 后 `MongoEventUtil.designFunction(function, parent())` |
| itemVisible() | 可见性 | `isVisible()` |
| loadChild() | 加载函数列表 | `client().listFunctions` 做增删更新，`doFilter`/`doSort`，成功后 expend |
| reloadChild() | 重载 | clearChild + loadChild |
| dbName()/client()/info()/infoName() | 委托父节点 | — |
| onPrimaryDoubleClick() | 双击 | 未加载则加载 |
| functionSize()/getFunctionSize() | 函数数量 | `client().functionSize(dbName)` 并缓存 |
| addFunction(MongoFunction) | 添加函数节点 | addChild `ShellMongoFunctionTreeItem` + sortChild |

- 调用链：`MongoDatabaseTreeItem.loadChild → new ShellMongoFunctionsTreeItem → loadChild → MongoClient.listFunctions`
- 调用链：`ShellMongoFunctionTreeItem.cloneFunction → ShellMongoFunctionsTreeItem.addFunction`

---

## ShellMongoFunctionsTreeItemValue
- 职责：函数类型节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoFunctionsTreeItemValue(ShellMongoFunctionsTreeItem) | 构造值 | 设置 richMode |
| item() | 获取节点 | 强转 |
| name() | 名称 | `I18nHelper.function()` |
| graphic() | 图标 | 惰性创建 `FunctionSVGGlyph` 并 disableTheme |
| graphicColor() | 图标颜色 | 子节点非空时 `Color.GREEN` |
| extra() | 附加文本 | `getFunctionSize()` 非空返回 `" (size)"` |
| extraColor() | 附加颜色 | `#228B22` |

- 调用链：`ShellMongoFunctionsTreeItem → setValue(new ShellMongoFunctionsTreeItemValue(this))`

---

## MongoGroupTreeItem
- 职责：连接分组树节点，实现 `MongoConnectManager`，管理分组内连接的增删、分组重命名/删除及拖拽归组。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoGroup | 分组对象 |
| connectStore | MongoConnectStore | 连接存储（单例） |
| groupStore | MongoGroupStore | 分组存储（单例） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoGroupTreeItem(MongoGroup, MongoTreeView) | 构造分组节点 | 设置值对象、展开状态、监听展开/收缩并 `groupStore.update` |
| getMenuItems() | 右键菜单 | addConnect/renameGroup/deleteGroup |
| rename() | 重命名分组 | 校验与存在性后 `groupStore.update` + refresh |
| delete() | 删除分组 | 分情况确认，`groupStore.delete`，子连接清除 groupId 并转移到父节点，remove |
| addConnect() | 添加连接 | 弹 `MongoConnectAddController` 并传 group |
| parent() | 父节点 | 强转 `MongoRootTreeItem` |
| addConnect(MongoConnect) | 添加连接节点 | `new MongoConnectTreeItem` 后 addConnectItem |
| addConnectItem(MongoConnectTreeItem) | 添加连接节点 | 校验非重复，更新 groupId 存库，addChild |
| addConnectItems(List) | 批量添加 | `addChild(list)` |
| delConnectItem(MongoConnectTreeItem) | 删除连接 | `connectStore.delete` 后 removeChild |
| getConnectItems() | 获取连接节点 | 遍历 richChildren 过滤 `MongoConnectTreeItem` |
| allowDrop()/allowDropNode(DragNodeItem)/onDropNode(DragNodeItem) | 拖拽 | 允许落点，拖入连接时从原处移除并 addConnectItem |
| value() | 获取分组对象 | — |

- 调用链：`MongoRootTreeItem.initChildes → new MongoGroupTreeItem`
- 调用链：`MongoGroupTreeItem.onDropNode → addConnectItem → connectStore.update`

---

## MongoGroupTreeItemValue
- 职责：分组树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoGroupTreeItemValue(MongoGroupTreeItem) | 构造值 | `super(item)` |
| item() | 获取节点 | 强转 |
| name() | 名称 | `item().value().getName()` |
| graphic() | 图标 | 惰性创建 `FolderSVGGlyph` |

- 调用链：`MongoGroupTreeItem → setValue(new MongoGroupTreeItemValue(this))`

---

## MongoQueriesTreeItem
- 职责：查询类型节点（"查询"分组），从 `MongoQueryStore` 加载查询列表并提供新增/添加查询。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| querySize | Integer | 查询数量缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoQueriesTreeItem(RichTreeView) | 构造查询类型节点 | setFilterable(true)、值对象、子节点变化监听 |
| parent() | 父节点 | 强转 `MongoDatabaseTreeItem` |
| getMenuItems() | 右键菜单 | addQuery/reload |
| addQuery() | 新增查询 | `MongoEventUtil.queryAdd(parent())` |
| itemVisible() | 可见性 | `isVisible()` |
| loadChild() | 加载查询列表 | Task 内 `MongoQueryStore.INSTANCE.list(info().getId(), dbName())` → `MongoQueryTreeItem` 列表 |
| reloadChild() | 重载 | clearChild + loadChild |
| addChild(MongoQuery) | 添加查询子节点 | addChild `MongoQueryTreeItem` |
| dbName()/client()/info() | 委托父节点 | — |
| onPrimaryDoubleClick() | 双击 | 未加载则加载 |
| querySize()/getQuerySize() | 查询数量 | `MongoQueryStore.INSTANCE.list(...)` 大小并缓存 |
| shellConnect() | shell 连接 | `parent().shellConnect()` |
| addQuery(MongoQuery) | 添加查询 | addChild + sortChild |

- 调用链：`MongoDatabaseTreeItem.loadChild → new MongoQueriesTreeItem → loadChild → MongoQueryStore.list`
- 调用链：`MongoQueriesTreeItem.addQuery → MongoEventUtil.queryAdd → MongoQueryOpenEvent/相关处理`

---

## MongoQueriesTreeItemValue
- 职责：查询类型节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoQueriesTreeItemValue(MongoQueriesTreeItem) | 构造值 | 设置 richMode |
| item() | 获取节点 | 强转 |
| name() | 名称 | `I18nHelper.queries()` |
| graphic() | 图标 | 惰性创建 `QuerySVGGlyph` 并 disableTheme |
| graphicColor() | 图标颜色 | 子节点非空时 `Color.GREEN` |
| extra() | 附加文本 | `getQuerySize()` 非空返回 `" (size)"` |
| extraColor() | 附加颜色 | `#228B22` |

- 调用链：`MongoQueriesTreeItem → setValue(new MongoQueriesTreeItemValue(this))`

---

## MongoQueryTreeItem
- 职责：查询树节点，提供查询的打开、重命名、删除（基于 `MongoQueryStore` 持久化）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | MongoQuery | 当前查询对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| value() | 获取查询对象 | — |
| MongoQueryTreeItem(MongoQuery, RichTreeView) | 构造查询节点 | setFilterable(true)、值对象 |
| parent() | 父节点 | 强转 `MongoQueriesTreeItem` |
| client()/info() | 委托父节点 | — |
| getMenuItems() | 右键菜单 | openQuery/renameQuery/deleteTable |
| delete() | 删除查询 | 确认后 `MongoQueryStore.INSTANCE.delete`，remove + `MongoEventUtil.queryDeleted` |
| rename() | 重命名查询 | 校验后更新 value、`MongoQueryStore.INSTANCE.update`、`MongoEventUtil.queryRenamed`、refresh |
| dbItem() | 所属数据库节点 | `parent().parent()` |
| dbName() | 数据库名称 | `parent().dbName()` |
| queryName() | 查询名称 | `value.getName()` |
| onPrimaryDoubleClick() | 双击打开 | `MongoEventUtil.queryOpen(value, dbItem())` |
| shellConnect() | shell 连接 | `client().getShellConnect()` |

- 调用链：`MongoQueriesTreeItem.loadChild → new MongoQueryTreeItem`
- 调用链：`MongoQueryTreeItem.rename → MongoQueryStore.update → MongoEventUtil.queryRenamed`

---

## MongoQueryTreeItemValue
- 职责：查询树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoQueryTreeItemValue(MongoQueryTreeItem) | 构造值 | `super(item)` |
| item() | 获取节点 | 强转 |
| graphic() | 图标 | 惰性创建 `QuerySVGGlyph` |
| name() | 名称 | `item().queryName()` |

- 调用链：`MongoQueryTreeItem → setValue(new MongoQueryTreeItemValue(this))`

---

## MongoRootTreeItem
- 职责：树根节点，实现 `MongoConnectManager`，初始化分组与连接、管理连接的增删查与拖拽。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| connectStore | MongoConnectStore | 连接存储（单例） |
| groupStore | MongoGroupStore | 分组存储（单例） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoRootTreeItem(MongoTreeView) | 构造根节点 | 设置值对象、`initChildes()` |
| initChildes() | 初始化子节点 | `groupStore.load()` 建分组，`connectStore.load()` 加连接 |
| getMenuItems() | 右键菜单 | addConnect/exportConnect/importConnect/addGroup，空时禁用 export |
| exportConnect() | 导出连接 | `connectStore.load()` 空则提示（实际导出逻辑待完善） |
| dragFile(List<File>) | 拖拽文件 | 校验单文件后 `parseConnect` |
| importConnect() | 导入连接 | 选择 json 后 `parseConnect` |
| parseConnect(File) | 解析连接文件 | 校验存在/目录/类型 json/非空 |
| addConnect() | 添加连接 | `StageManager.showStage(MongoConnectAddController...)` |
| addGroup() | 添加分组 | 校验名称与存在性后 `groupStore.insert` + addChild |
| getGroupItem(String groupId) | 按 id 获取分组节点 | 遍历 `getGroupItems()` 匹配 gid |
| getGroupItems() | 获取分组节点列表 | 遍历 richChildren 过滤 `MongoGroupTreeItem` |
| infoAdd(MongoConnect) | 连接新增处理 | `addConnect(info)` |
| infoUpdate(MongoConnect) | 连接变更处理 | 遍历连接/分组节点匹配 object 相等后刷新值 |
| addConnect(MongoConnect) | 添加连接 | 有分组则加入分组，否则 addChild `MongoConnectTreeItem` |
| addConnectItem(MongoConnectTreeItem) | 添加连接节点 | 清 groupId、addChild、expend |
| addConnectItems(List) | 批量添加 | addChild + expend |
| delConnectItem(MongoConnectTreeItem) | 删除连接 | `connectStore.delete` 后 removeChild |
| getConnectItems() | 获取全部连接节点 | 遍历连接与分组连接 |
| getConnectedItems() | 获取已连接节点 | 覆盖默认实现，遍历连接与分组过滤 `isConnected()` |
| allowDrop()/allowDropNode(DragNodeItem)/onDropNode(DragNodeItem) | 拖拽 | 允许落点，连接拖入时移出原处并 addConnectItem |

- 调用链：`new MongoTreeView() → new MongoRootTreeItem(this) → initChildes`
- 调用链：`MongoRootTreeItem.onDropNode → addConnectItem → connectStore.update`

---

## MongoRootTreeItemValue
- 职责：根节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| name() | 名称 | `I18nHelper.database()` |
| graphic() | 图标 | 惰性创建 `DatabaseSVGGlyph` |

- 调用链：`MongoRootTreeItem → setValue(new MongoRootTreeItemValue())`

---

## MongoTerminalTreeItem
- 职责：终端树节点，双击打开数据库终端。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoTerminalTreeItem(RichTreeView) | 构造终端节点 | 设置 `MongoTerminalTreeItemValue` |
| parent() | 父节点 | 强转 `MongoDatabaseTreeItem` |
| shellConnect() | shell 连接 | `parent().shellConnect()` |
| client() | 客户端 | `parent().client()` |
| onPrimaryDoubleClick() | 双击打开终端 | `MongoEventUtil.terminalOpen(client(), parent().dbName())` |

- 调用链：`MongoDatabaseTreeItem.loadChild → new MongoTerminalTreeItem → 双击 → MongoEventUtil.terminalOpen`

---

## MongoTerminalTreeItemValue
- 职责：终端树节点展示值。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| graphic() | 图标 | 惰性创建 `TerminalSVGGlyph` |
| name() | 名称 | `I18nHelper.terminal()` |

- 调用链：`MongoTerminalTreeItem → setValue(new MongoTerminalTreeItemValue())`
