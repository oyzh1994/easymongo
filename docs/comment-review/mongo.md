# easymongo `mongo` 包代码审查文档

`cn.oyzh.easymongo.mongo` 及其子包 `condition` 封装了 MongoDB 的客户端连接、数据库/集合/桶/函数/记录的领域模型与 CRUD 操作，以及查询过滤条件的构建。底层依赖 mongodb driver（`com.mongodb.*`、`org.bson.*`）。

说明：`MongoRecordData` 整文件被注释，属死代码，不在本文档覆盖范围；`MongoConnState` 为枚举，其余为类。

覆盖类/接口/枚举数：40

---

## DBObjectList
- 职责：数据库对象状态列表（`ArrayList` 子类），维护对象的新增/变更/删除/正常状态并提供筛选。

- 字段：（均为常量）

| 字段 | 类型 | 含义 |
|---|---|---|
| TYPE_NORMAL | byte | 类型：正常（0） |
| TYPE_DELETED | byte | 类型：已删除（1） |
| TYPE_CREATED | byte | 类型：已新增（2） |
| TYPE_CHANGED | byte | 类型：已变更（3） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| isChanged() | 是否存在状态非空对象 | 遍历判断 `getStatus()` 是否非空 |
| createdList()/changedList()/deletedList()/normalList() | 按状态筛选列表 | stream filter 调用对应静态判定方法 |
| filterList(byte... types) | 按类型集合筛选 | 按类型分发到各列表方法并合并 |
| add(S) | 添加（覆盖） | 非 null 才 `super.add` |
| remove(S)/contains(S) | 移除/包含 | 委托 `super` |
| hasDeleted()/hasCreated()/hasChanged()/hasNormal() | 是否含对应状态对象 | 遍历判定 |
| isDeleted(DBObjectStatus)/isCreated/isChanged/isNormal | 静态状态判定 | 依据 `isDeleted/isCreated/isChanged` 组合判断 |

- 调用链：`MongoColumns extends DBObjectList<MongoColumn>`
- 调用链：`部署/保存逻辑 → createdList/changedList/deletedList → 生成差异 SQL`

---

## DBObjectStatus
- 职责：数据库对象状态基类，记录新增/变更/删除标记、原始数据及状态文本。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| changedProperty | SimpleBooleanProperty | 对象是否变更 |
| deletedProperty | SimpleBooleanProperty | 对象是否删除 |
| createdProperty | SimpleBooleanProperty | 对象是否新增 |
| changedFlag | Map<String,Boolean> | 变更数据 |
| originalData | Map<String,Object> | 原始数据 |
| statusProperty | SimpleStringProperty | 状态文本 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| changedProperty()/deletedProperty()/createdProperty() | 惰性获取属性 | 各自 new `SimpleBooleanProperty` |
| changedFlag()/setChangedFlag(key,value) | 变更标记读写 | 值真则 put，否则 remove，并 `setChanged(!isEmpty)` |
| clearChangedFlag() | 清除变更标记 | 清空 map |
| originalData()/putOriginalData(key,value)/getOriginalData(key) | 原始数据读写 | put 时对比旧值触发 `setChangedFlag` |
| clearOriginalData() | 清除原始数据 | 清空 map |
| checkOriginalData(key,currentData) | 校验是否变化 | `!Objects.equals(getOriginalData(key), currentData)` |
| initStatus() | 初始化状态（可重写） | 空实现 |
| setChanged/isChanged/setDeleted/isDeleted/setCreated/isCreated | 状态读写 | set 时触发 `updateStatus()` |
| clearStatus() | 清除状态 | 置三种状态为 false 并清变更标记 |
| updateStatus() | 更新状态文本 | 新增="+"、变更="*"、否则="" |
| statusProperty()/getStatus() | 状态属性/文本 | — |

- 调用链：`MongoColumn/MongoRecord extends DBObjectStatus`

---

## DBStatusListener
- 职责：数据库对象状态监听器抽象基类，绑定 `ChangeListener<Object>` 并提供标识。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| key | String | 监听器标识 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| DBStatusListener() | 用随机 UUID 构造 | 委托 `this(UUID...)` |
| DBStatusListener(String key) | 用指定标识构造 | 记录 key 并 `DBStatusListenerManager.addListener(this)` |
| DBStatusListener(String dbName, String tableName) | 用库/表构造 | 拼 key `db:table` |
| DBStatusListener(String dbName, String schema, String tableName) | 用库/模式/表构造 | 拼 key `db:schema:table` |
| destroy() | 销毁 | `DBStatusListenerManager.removeListener(this)` |
| getKey() | 获取标识 | — |

- 调用链：`new DBStatusListener(...) → DBStatusListenerManager.addListener`
- 调用链：`DBStatusListenerManager.bindListener(node, listener) → node 属性 addListener(listener)`

---

## DBStatusListenerManager
- 职责：数据库对象状态监听器的静态注册表与节点绑定工具。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| LISTENERS | static Map<String,DBStatusListener> | 监听器缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| addListener(DBStatusListener) | 添加监听器 | `LISTENERS.put(getKey(), listener)` |
| removeListener(DBStatusListener) | 移除监听器 | `LISTENERS.remove(getKey())` |
| getListener(String key) | 获取监听器 | `LISTENERS.get(key)` |
| bindListener(Object node, DBStatusListener) | 绑定监听器 | 按 TextInputControl/ComboBox/CheckBox/Property 类型相应 addListener |
| unbindListener(Object node, DBStatusListener) | 解绑监听器 | 对应 removeListener |

- 调用链：`MongoRecordProperty.setChanged → DBStatusListenerManager.getListener(db:collection) → listener.changed`

---

## MongoBucket
- 职责：MongoDB GridFS 存储桶领域对象。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 桶名称 |
| dbName | String | 数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getName/setName | 桶名称读写 | — |
| getDbName/setDbName | 数据库名称读写 | — |
| compare(MongoBucket) | 比较 | name 与 dbName 均相等 |
| copy(MongoBucket) | 拷贝 | 复制 name、dbName |

- 调用链：`MongoClient.listBuckets → new MongoBucket`

---

## MongoClient
- 职责：MongoDB 客户端封装（`Closeable`），管理连接状态、数据库/集合/桶/函数/记录的 CRUD 与脚本执行。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| shellConnect | MongoConnect | MongoDB 连接信息 |
| state | static SimpleObjectProperty<MongoConnState> | 连接状态 |
| mongoClient | com.mongodb.client.MongoClient | mongo 客户端 |
| engine | MongoScriptEngine | 脚本引擎 |
| version | String | 版本号缓存 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoClient(MongoConnect) | 构造客户端 | 记录 shellConnect，监听 state 变化并 post connectionClosed/connectionConnected |
| connectName()/iid()/getShellConnect() | 名称/标识/连接信息 | 委托 shellConnect |
| isConnected()/isConnecting()/isClosed() | 连接状态 | 依据 `state` 判定 |
| close() | 关闭 | `mongoClient.close()` 并 `state.set(CLOSED)` |
| stateProperty()/addStateListener(...) | 状态属性/监听 | — |
| initHost()/initClient() | 计算地址/初始化客户端 | 构建 `MongoClientSettings`（超时、密码认证），`MongoClients.create` |
| start() | 启动连接 | `state.set(CONNECTING)` → 触发一次网络请求 → `state.set(CONNECTED)`，异常置 FAILED 抛 `MongoException` |
| database(String)/listDatabases()/listDatabaseNames() | 数据库获取/列举 | 处理 "not authorized" 回退到 authDatabase |
| createDatabase/existDatabase/alterDatabase/dropDatabase | 数据库操作 | 建库以建 `_empty_` 集合实现 |
| collection(dbName, collectionName) | 获取底层集合 | `mongoClient.getDatabase(...).getCollection(...)` |
| dropCollection/listCollections/listCollectionNames | 集合操作 | 过滤 `MongoRecordUtil.isCollection` |
| selectCollectionRecords(MongoSelectRecordParam) | 查询记录 | `MongoConditionUtil.buildCondition` + skip/limit → `MongoRecordUtil.docToRecord` |
| selectCollectionRecord(db, collection, id) | 查询单条 | `Filters.eq(_id, id)` |
| selectCollectionRecordCount(param) | 记录数 | `countDocuments` |
| insertCollectionRecord(MongoRecord) | 插入 | 组装 Document 调 `insertOne` |
| insertCollectionRecord(List) | 批量插入 | 组装 Document 调 `insertMany` |
| deleteCollectionRecord(MongoRecord) | 删除 | 按 _id `deleteOne` |
| updateCollectionRecord(MongoRecord) | 更新 | 按 _id 组装 `Updates.set/unset` 调 `updateOne` |
| clearCollection/renameCollection/createCollection | 清空/重命名/创建集合 | 委托 driver |
| listBuckets/listBucketNames/bucket/createBucket/dropBucket/clearBucket | 桶操作 | GridFS；建桶通过上传空数据实现 |
| toBucketRecord(MongoColumns, GridFSFile) | 转换为记录 | 填充 `_id/filename/length/chunkSize/uploadDate/metadata` |
| selectBucketRecords/selectBucketRecord/selectBucketRecordCount | 桶记录查询 | GridFS find + `bucketColumns` |
| bucketColumns() | 桶字段列表 | 构造 `_id/filename/length/chunkSize/uploadDate/metadata` |
| uploadBucketRecord/reuploadBucketRecord/downloadBucketRecord/deleteBucketRecord/updateBucketRecord | 桶记录增删改 | GridFS 流操作 |
| selectColumns(param) | 字段列表 | `selectCollectionRecords` 后 `MongoRecordUtil.columns` |
| selectVersion() | 查询版本 | `admin.runCommand(buildInfo)` |
| executeSingleScript(db, script) | 执行单段脚本 | `shellEngine().eval` + `parseResult` |
| parseResult(...) | 解析脚本结果 | 按 Cursor/List/DeleteResult/InsertResult/UpdateResult 分派 |
| toMongoRecord(...) | 转记录 | Document 走 `docToRecord`，其他包成单列记录 |
| executeScript(db, script) | 执行脚本 | `MongoScriptParser` 解析后逐条 `executeSingleScript` |
| listFunctions/createFunction/alertFunction/selectFunction/dropFunction/renameFunction/functionSize | 函数操作 | 基于 `system.js` 集合 |
| eval(db, script) | 执行脚本 | `shellEngine().eval` |

- 调用链：`MongoConnectTreeItem.connect → MongoClient.start → mongoClient.listDatabases → MongoDatabaseTreeItem`
- 调用链：`MongoClient.selectCollectionRecords → MongoConditionUtil.buildCondition → MongoRecordUtil.docToRecord`
- 调用链：`MongoClient.executeScript → MongoScriptParser.parseScript → executeSingleScript → shellEngine.eval`

---

## MongoClientUtil
- 职责：MongoDB 客户端工具类。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| newClient(MongoConnect) | 创建客户端 | `new MongoClient(connect)` |

- 调用链：`业务代码 → MongoClientUtil.newClient → new MongoClient`

---

## MongoCollection
- 职责：MongoDB 集合领域对象。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 集合名称 |
| dbName | String | 数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getName/setName | 集合名称读写 | — |
| getDbName/setDbName | 数据库名称读写 | — |
| compare(MongoCollection) | 比较 | name 与 dbName 均相等 |
| copy(MongoCollection) | 拷贝 | 复制 name、dbName |

- 调用链：`MongoClient.listCollections → new MongoCollection`

---

## MongoColumn
- 职责：数据库字段领域对象，含类型判定、默认值、变更追踪。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 库名称 |
| collectionName | String | 集合名称 |
| typeProperty | StringProperty | 字段类型 |
| value | String | 字段值 |
| name | String | 名称 |
| aliasName | String | 别名（优先 name 显示） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoColumn()/MongoColumn(name)/MongoColumn(name, aliasName) | 构造函数 | — |
| isNameChanged()/originalName() | 名称变更/原名 | 基于 `checkOriginalData`/`getOriginalData` |
| setType(String)/setValue(String)/setName(String) | 设置字段 | 同步 `putOriginalData` |
| supportDigits/supportInt32/supportInt64/supportString/supportDate/supportBoolean/supportList/supportObject/supportBinary/supportObjectId/supportCode | 类型判定 | 依据 type 字符串比较 |
| initStatus() | 初始化状态 | value 为 null 时 `setValue(null)` |
| copy(MongoColumn) | 拷贝 | 复制 name/type/value/dbName/collectionName |
| isInvalid() | 是否无效 | name 或 type 为空 |
| getType/getValue/getName/getDbName/getCollectionName 等 | getter/setter | — |
| is_id() | 是否 _id | `MongoUtil.ID` 比较 |
| displayName() | 显示名称 | 别名优先 |
| supportInteger() | 是否整数 | int/long |
| defaultValue() | 默认值 | 按类型返回 0/0L/0d/{} /[] /日期/byte[]/false/function |

- 调用链：`MongoRecordUtil.docToRecord/columns → new MongoColumn`

---

## MongoColumns
- 职责：数据库字段列表，提供按名称查找与名称集合。

- 字段：无（继承 `DBObjectList<MongoColumn>`）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoColumns()/MongoColumns(List<MongoColumn>) | 构造 | 后者 `addAll` |
| exists(name) | 是否存在字段 | `column(name) != null` |
| column(name) | 按名获取字段 | 遍历 `equalsAnyIgnoreCase` |
| index(name) | 字段索引 | 遍历比对 |
| collectionName()/dbName() | 首个字段的集合名/库名 | 遍历取首 |
| columnNames() | 字段名列表 | 遍历收集 |

- 调用链：`MongoRecordUtil.columns → new MongoColumns`

---

## MongoConnState
- 职责：连接状态枚举（未初始化/已连接/连接中/已关闭/失败/中断/重连）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| isConnected()（抽象） | 是否已连接 | 各枚举常量实现：CONNECTED、RECONNECTED 返回 true，其余 false |

- 调用链：`MongoClient.state → MongoConnState.CONNECTED/CONNECTING/CLOSED/FAILED`

---

## MongoDatabase
- 职责：MongoDB 数据库领域对象。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 数据库名称 |
| sizeOnDisk | Double | 磁盘占用大小 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getName/setName | 名称读写 | — |
| getSizeOnDisk/setSizeOnDisk | 磁盘占用读写 | — |

- 调用链：`MongoClient.listDatabases → new MongoDatabase`

---

## MongoFunction
- 职责：MongoDB 函数领域对象。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 函数名称 |
| code | String | 函数代码 |
| dbName | String | 数据库名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getName/setName/getCode/setCode/getDbName/setDbName | 读写 | — |
| isNew() | 是否新函数 | code 为空 |
| copy(MongoFunction) | 拷贝 | 复制 name/code/dbName |
| compare(MongoFunction) | 比较 | name 与 dbName 相等 |

- 调用链：`MongoClient.listFunctions/selectFunction → new MongoFunction`

---

## MongoRecord
- 职责：数据库记录领域对象，持有字段列表与字段值属性，支持变更追踪、拷贝与纠正字段。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| readonly | boolean | 是否只读 |
| editable | boolean | 是否可编辑 |
| columns | MongoColumns | 字段列表 |
| properties | HashMap<String,MongoRecordProperty> | 数据（字段名→属性） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoRecord(columns)/MongoRecord(columns, readonly) 等多构造 | 记录构造 | 包装 columns |
| getColumns() | 获取字段列表 | — |
| putValue(String, Object)/putValue(MongoColumn, Object) | 添加数据 | 建/改 `MongoRecordProperty` 并监听 changed |
| getValue(column)/getOriginal(column) | 取值/原始值 | 委托 property |
| columns() | 字段名集合 | `properties.keySet()` |
| getProperty(key)/hasProperty(...)/removeProperty(key)/clearProperty() | 属性操作 | map 操作 |
| update(Map<String,Object>) | 批量更新 | 遍历 putValue |
| isChanged() | 是否变更（覆盖） | 自省 + 任一 property 变更 |
| clearStatus() | 清除状态（覆盖） | 各 property 清变更并 `updateOriginal` 后 `super.clearStatus` |
| discard() | 抛弃变更 | 各 property `discard` |
| copy(MongoRecord) | 拷贝（覆盖） | 对齐 columns 增删，拷贝属性值，去除多余列 |
| correctColumns(MongoColumns) | 纠正字段 | 补齐缺失列并置 null |
| isColumnChanged(String) | 指定列是否变更 | 现状恒 false |
| toMap() | 转 Map | 遍历 properties 取值 |
| destroy() | 销毁 | 销毁各 property 并清空 |
| isEditable/setEditable | 可编辑读写 | — |
| set_id(BsonValue) | 设置 _id | `putValue(MongoUtil.ID, ...)` |
| column(columnName)/_idColumn() | 字段获取 | 委托 columns |
| _idValue() | _id 值 | `getProperty(_id).getOriginal()` |

- 调用链：`MongoRecordUtil.docToRecord → new MongoRecord(columns) → putValue`
- 调用链：`MongoRecord.copy → MongoColumn.copy / putValue`

---

## MongoRecordFilter
- 职责：记录过滤条件行，持有字段/条件/值/连接符，并生成对应 UI 组件与 Bson 条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| value | Object | 值 |
| enabled | boolean | 是否已启用（默认 true） |
| joinSymbol | String | 连接符号 |
| condition | MongoCondition | 条件 |
| column | MongoColumn | 字段 |
| columns | List<MongoColumn> | 字段列表 |
| valueBox | FXHBox | 值组件 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| value() | 获取值 | 有值组件则从组件取值 |
| getValueControl()/updateValueControl() | 值组件 | `MongoConditionUtil.generateNode/setNodeVal` 生成节点 |
| getColumnControl() | 字段组件 | 返回 `MongoColumnComboBox` 并绑定选择 |
| getConditionControl() | 条件组件 | 返回 `MongoConditionComboBox` |
| getEnabledControl() | 启用组件 | 返回 `FXCheckBox` |
| getJoinSymbolControl() | 连接符组件 | 返回 `MongoJoinSymbolComboBox` |
| column() | 字段名 | `column.getName()` |
| condition() | 生成 Bson | `condition.wrapCondition(column, value())` |
| isRequireCondition() | 是否需要条件 | 委托条件 |
| getValue/setValue/getJoinSymbol/setJoinSymbol/getCondition/setCondition/getColumn/setColumn/getColumns/setColumns/isEnabled/setEnabled | 读写 | — |

- 调用链：`MongoConditionUtil.buildCondition(filters) → filter.condition() → MongoCondition.wrapCondition`

---

## MongoRecordProperty
- 职责：数据库记录字段属性（`SimpleObjectProperty<Object>`），桥接字段值与编辑控件并追踪变更。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| changedProperty | SimpleBooleanProperty | 是否变更 |
| column | MongoColumn | 表字段 |
| record | MongoRecord | 表记录 |
| original | Object | 原始数据 |
| setToNullFlag | boolean | 设置为 null 标志位 |
| readonly | boolean | 只读模式 |
| node | Node | 编辑控件节点 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoRecordProperty(record, column, value, readonly) | 构造 | _id 走 `idValue`，记录 original |
| get() | 取值（覆盖） | 只读/未变更/无节点返回原值，否则从节点取值 |
| set(Object) | 设值（覆盖） | 旧值非空且不同则 `setChanged(true)`；类型变化时刷新节点 |
| getValue() | 获取控件/值（覆盖） | 只读返回值，否则惰性 `initNode` |
| refreshNode()/initNode() | 初始化/刷新节点 | `MongoRecordUtil.getNode` + 绑定快捷键 |
| discard() | 抛弃变更 | 恢复节点值并清变更 |
| changedProperty()/isChanged()/setChanged(...) | 变更读写 | setChanged 通知 `DBStatusListenerManager` 监听器 |
| updateOriginal() | 更新原始值 | _id 之外从节点取当前值设 original |
| getControl()/getNode() | 获取控件 | — |
| vCopy()/vPaste() | 复制/粘贴节点值 | `ClipboardUtil` |
| vCopyAsInsertSql()/vCopyAsUpdateSql() | 复制为脚本 | `MongoDataUtil.toInsertScript/toUpdateScript` |
| vSetToNull()/vSetToEmptyString() | 置 null/空串 | 标记并 `MongoNodeUtil` 修改节点 |
| getColumn/setColumn/getOriginal/setOriginal/isReadonly | 读写 | — |
| destroy() | 销毁 | 销毁节点并清引用 |

- 调用链：`MongoRecord.putValue → new MongoRecordProperty → changedProperty.addListener`
- 调用链：`MongoRecordProperty.setChanged → DBStatusListenerManager.getListener → listener.changed`

---

## MongoSelectRecordParam
- 职责：查询记录参数载体（分页/库集合/列/过滤条件）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| start | Long | 起始位置 |
| limit | Long | 查询数量 |
| dbName | String | 数据库名称 |
| collectionName | String | 集合名称 |
| readonly | boolean | 是否只读 |
| columns | List<MongoColumn> | 列集合 |
| filters | List<MongoRecordFilter> | 过滤条件集合 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoSelectRecordParam()/MongoSelectRecordParam(dbName, collectionName) | 构造 | — |
| hasPageControl() | 是否含分页 | start 与 limit 均非空 |
| getStart/setStart/getLimit/setLimit/getDbName/setDbName/getCollectionName/setCollectionName/isReadonly/setReadonly/getColumns/setColumns/getFilters/setFilters | 读写 | — |

- 调用链：`MongoCollectionTreeItem.recordPage → new MongoSelectRecordParam → MongoClient.selectCollectionRecords`

---

## MonogoHelper
- 职责：MongoDB 辅助类（占位，暂无实现）。

- 字段：无

- 方法：无

- 调用链：无

---

## MongoCondition
- 职责：查询条件抽象基类，定义条件名称/值/是否需要条件及 `wrapCondition` 契约。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 名称 |
| value | String | 值 |
| requireCondition | boolean | 需要条件标志位（默认 true） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCondition()/MongoCondition(name, value)/MongoCondition(name, value, requireCondition) | 构造 | — |
| wrapCondition(columnName) | 构建条件 | 委托 `wrapCondition(columnName, null)` |
| wrapCondition(columnName, condition) | 构建条件（可重写） | 基类返回 null |
| getName/setName/getValue/setValue/isRequireCondition/setRequireCondition | 读写 | — |

- 调用链：`MongoRecordFilter.condition() → MongoCondition.wrapCondition`

---

## MongoConditionUtil
- 职责：条件工具类，集中提供全部条件实例、过滤条件组装、UI 节点生成与 _id 正则条件。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| conditions() | 全部条件列表 | 汇聚 20 个条件单例 |
| buildCondition(List<MongoRecordFilter>) | 构建 Bson 条件 | 跳过未启用项，按 joinSymbol and/or 组合 |
| isInCondition(MongoCondition)/isBetweenCondition(MongoCondition) | 条件类型判定 | 与 IN/BETWEEN 单例比较 |
| generateNode(column, condition) | 生成值节点 | IN 用 ClearableTextField，BETWEEN 双节点，否则 `MongoNodeUtil.generateNode` |
| setNodeVal(controls, value)/getNodeVal(controls) | 节点取值/设值 | 单/多组件适配 |
| idFilterRegex(Pattern)/idFilterRegexNot(Pattern) | _id 正则条件 | `Filters.expr($regexMatch/$not)` 配合 `$toString` |

- 调用链：`MongoClient.selectCollectionRecords → MongoConditionUtil.buildCondition(filters)`
- 调用链：`MongoRecordFilter.getValueControl → MongoConditionUtil.generateNode/setNodeVal`

---

## MongoBetweenCondition
- 职责：BETWEEN 介于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoBetweenCondition | 介于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoBetweenCondition()/MongoBetweenCondition(name, value) | 构造 | 名称 "BETWEEN" |
| wrapCondition(columnName, condition) | 构建条件 | 取 list 首尾，_id 走 `$expr`+`$toString`，否则 `and(exists, gte, lte)` |

- 调用链：`MongoConditionUtil.conditions → MongoBetweenCondition.INSTANCE`

---

## MongoContainsCondition
- 职责：LIKE 包含条件（不区分大小写正则）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoContainsCondition | 包含条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoContainsCondition()/MongoContainsCondition(name, value) | 构造 | 名称 "LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | `Pattern.quote` + CASE_INSENSITIVE；_id 用 `idFilterRegex`，否则 `regex` |

- 调用链：`MongoConditionUtil.conditions → MongoContainsCondition.INSTANCE`

---

## MongoEmptyCondition
- 职责：= '' 为空条件（无需输入值）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoEmptyCondition | 为空条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoEmptyCondition() | 构造 | 名称 "=''"，requireCondition=false |
| wrapCondition(columnName, condition) | 构建条件 | `Filters.eq(columnName, "")` |

- 调用链：`MongoConditionUtil.conditions → MongoEmptyCondition.INSTANCE`

---

## MongoEndWithCondition
- 职责：LIKE 以…结尾条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoEndWithCondition | 结束以条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoEndWithCondition()/MongoEndWithCondition(name, value) | 构造 | 名称 "LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | 正则加 `$` 后缀，_id 用 `idFilterRegex` |

- 调用链：`MongoConditionUtil.conditions → MongoEndWithCondition.INSTANCE`

---

## MongoEqCondition
- 职责：= 等于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoEqCondition | 等于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoEqCondition() | 构造 | 名称 "=" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`+`$toString`，否则 `and(exists, eq)` |

- 调用链：`MongoConditionUtil.conditions → MongoEqCondition.INSTANCE`

---

## MongoGtCondition
- 职责：> 大于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoGtCondition | 大于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoGtCondition() | 构造 | 名称 ">" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, gt)` |

- 调用链：`MongoConditionUtil.conditions → MongoGtCondition.INSTANCE`

---

## MongoGtEqCondition
- 职责：>= 大于等于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoGtEqCondition | 大于等于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoGtEqCondition() | 构造 | 名称 ">=" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, gte)` |

- 调用链：`MongoConditionUtil.conditions → MongoGtEqCondition.INSTANCE`

---

## MongoInListCondition
- 职责：IN 在列表条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoInListCondition | 在列表条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoInListCondition()/MongoInListCondition(name, value) | 构造 | 名称 "IN" |
| wrapCondition(columnName, condition) | 构建条件 | 字符串按逗号拆分或 List；_id 走 `$expr`+`$toString`，否则 `and(exists, in)` |

- 调用链：`MongoConditionUtil.conditions → MongoInListCondition.INSTANCE`

---

## MongoLtCondition
- 职责：< 小于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoLtCondition | 小于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoLtCondition() | 构造 | 名称 "<" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, lt)` |

- 调用链：`MongoConditionUtil.conditions → MongoLtCondition.INSTANCE`

---

## MongoLtEqCondition
- 职责：<= 小于等于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoLtEqCondition | 小于等于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoLtEqCondition() | 构造 | 名称 "<=" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, lte)` |

- 调用链：`MongoConditionUtil.conditions → MongoLtEqCondition.INSTANCE`

---

## MongoNotBetweenCondition
- 职责：NOT BETWEEN 不介于条件（继承 `MongoBetweenCondition`）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotBetweenCondition | 不介于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotBetweenCondition() | 构造 | 名称 "NOT BETWEEN" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$or`，否则 `and(exists, or(lt, gt))` |

- 调用链：`MongoConditionUtil.conditions → MongoNotBetweenCondition.INSTANCE`

---

## MongoNotContainsCondition
- 职责：NOT LIKE 不包含条件（继承 `MongoContainsCondition`）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotContainsCondition | 不包含条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotContainsCondition() | 构造 | 名称 "NOT LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | _id 用 `idFilterRegexNot`，否则 `and(exists, not(regex))` |

- 调用链：`MongoConditionUtil.conditions → MongoNotContainsCondition.INSTANCE`

---

## MongoNotEmptyCondition
- 职责：!= '' 不为空条件（无需输入值）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotEmptyCondition | 不为空条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotEmptyCondition() | 构造 | 名称 "!=''"，requireCondition=false |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `Filters.ne(columnName, "")` |

- 调用链：`MongoConditionUtil.conditions → MongoNotEmptyCondition.INSTANCE`

---

## MongoNotEndWithCondition
- 职责：NOT LIKE 不以…结尾条件（继承 `MongoEndWithCondition`）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotEndWithCondition | 不是结束以条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotEndWithCondition() | 构造 | 名称 "NOT LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | 正则加 `$`，_id 用 `idFilterRegexNot` |

- 调用链：`MongoConditionUtil.conditions → MongoNotEndWithCondition.INSTANCE`

---

## MongoNotEqCondition
- 职责：!= 不等于条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotEqCondition | 不等于条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotEqCondition() | 构造 | 名称 "!=" |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, ne)` |

- 调用链：`MongoConditionUtil.conditions → MongoNotEqCondition.INSTANCE`

---

## MongoNotInListCondition
- 职责：NOT IN 不在列表条件（继承 `MongoInListCondition`）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotInListCondition | 不在列表条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotInListCondition() | 构造 | 名称 "NOT IN" |
| wrapCondition(columnName, condition) | 构建条件 | 字符串按逗号拆分；_id 走 `$not`+`$in`，否则 `and(exists, nin)` |

- 调用链：`MongoConditionUtil.conditions → MongoNotInListCondition.INSTANCE`

---

## MongoNotNullCondition
- 职责：IS NOT NULL 条件（无需输入值）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotNullCondition | 不是 NULL 条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotNullCondition() | 构造 | 名称 "IS NOT NULL"，requireCondition=false |
| wrapCondition(columnName, condition) | 构建条件 | _id 走 `$expr`，否则 `and(exists, ne(null))` |

- 调用链：`MongoConditionUtil.conditions → MongoNotNullCondition.INSTANCE`

---

## MongoNotStartWithCondition
- 职责：NOT LIKE 不以…开始条件（继承 `MongoStartWithCondition`）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNotStartWithCondition | 不是开始以条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNotStartWithCondition() | 构造 | 名称 "NOT LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | 正则加 `^`，_id 用 `idFilterRegexNot` |

- 调用链：`MongoConditionUtil.conditions → MongoNotStartWithCondition.INSTANCE`

---

## MongoNullCondition
- 职责：IS NULL 条件（无需输入值）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoNullCondition | 是 NULL 条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoNullCondition() | 构造 | 名称 "IS NULL"，requireCondition=false |
| wrapCondition(columnName, condition) | 构建条件 | `or(exists(false), eq(null))` |

- 调用链：`MongoConditionUtil.conditions → MongoNullCondition.INSTANCE`

---

## MongoStartWithCondition
- 职责：LIKE 以…开始条件。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| INSTANCE | static MongoStartWithCondition | 开始以条件实例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoStartWithCondition()/MongoStartWithCondition(name, value) | 构造 | 名称 "LIKE" |
| wrapCondition(columnName, condition) | 构建条件 | 正则加 `^`，_id 用 `idFilterRegex` |

- 调用链：`MongoConditionUtil.conditions → MongoStartWithCondition.INSTANCE`
