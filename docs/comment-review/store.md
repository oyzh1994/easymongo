# store 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/store/`
>
> 说明：各 Store 均继承 `cn.oyzh.store.jdbc` 下的 JDBC 存储基类，采用单例 `INSTANCE` 模式。

## MongoStoreUtil

- 职责：MongoDB 存储工具类，负责 H2 嵌入式数据库的初始化。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `init()` | 执行存储初始化 | 设置 `JdbcConst` 缓存 65535、页大小 1024、方言 `JdbcDialect.H2`、数据文件为 `MongoConst.getStorePath()+"db"`；调用 `JdbcManager.takeoff()` 启动；捕获「Database may be already in use」异常时弹出 `I18nHelper.programTip1()` 警告 |

- 调用链：`EasyMongoApp.main → MongoStoreUtil.init → JdbcManager.takeoff`

## MongoConnectStore

- 职责：MongoDB 连接存储，委托 `JdbcStandardStore<MongoConnect>`，并联动维护 SSH 配置。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoConnectStore`（static final） | 单例 |
| `sshConfigStore` | `MongoSSHConfigStore`（final） | SSH 配置存储，取自 `MongoSSHConfigStore.INSTANCE` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `load()` | 加载连接列表 | `super.selectList()` |
| `replace(MongoConnect)` | 新增或更新连接并同步 SSH 配置 | 存在（`exist(id)`）则 `update` 否则 `insert`；若 `sshConfig` 非空则 `setIid(id)` 后 `sshConfigStore.replace`，否则 `sshConfigStore.deleteByIid(id)` |
| `delete(MongoConnect)` | 删除连接并级联删除 SSH 配置 | `super.delete` 成功后 `sshConfigStore.deleteByIid(id)` |
| `modelClass()` | 返回模型类 | `MongoConnect.class` |

- 调用链：
  - `MongoConnectStore.replace → MongoSSHConfigStore.replace / deleteByIid`
  - `MongoConnectStore.delete → MongoSSHConfigStore.deleteByIid`

## MongoGroupStore

- 职责：MongoDB 连接分组存储，映射 `MongoGroup`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoGroupStore`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `load()` | 加载分组列表 | `super.selectList()` |
| `replace(MongoGroup)` | 存在则更新否则新增 | 按 `name` 或 `gid` 判断存在 |
| `delete(String)` | 按名称删除分组 | 构造 `DeleteParam`（`QueryParam("name", name)`）后删除 |
| `exist(String)` | 名称是否存在 | 构造 `Map{name}` 调用 `super.exist` |
| `modelClass()` | 返回模型类 | `MongoGroup.class` |

- 调用链：`MongoGroupStore.replace → exist/update/insert`

## MongoQueryStore

- 职责：MongoDB 查询存储，映射 `MongoQuery`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoQueryStore`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `list(iid, dbName)` | 按连接 id + 数据库名查询 | 构造 `SelectParam` 组合 `iid`、`dbName` 两个 `QueryParam` 后 `selectList` |
| `replace(MongoQuery)` | 存在则更新否则新增 | 按 `uid` 判断，`!exist(uid)` 则 `insert`，否则 `update` |
| `deleteByIid(String)` | 按连接 id 删除查询 | 条件为 `StringUtil.isEmpty(iid)` 时才执行删除（疑似应为 `isNotEmpty`，存在逻辑疑点，建议复核） |
| `modelClass()` | 返回模型类 | `MongoQuery.class` |

- 调用链：`MongoQueryStore.list(iid, dbName) → selectList(SelectParam)`

## MongoSSHConfigStore

- 职责：MongoDB 连接的 SSH 配置存储，映射 `MongoSSHConfig`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoSSHConfigStore`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `replace(MongoSSHConfig)` | 存在则更新否则新增 | 按 `iid` 判断（`super.exist(iid)`） |
| `deleteByIid(String)` | 按连接 id 删除 | `DeleteParam` + `QueryParam("iid", iid)` |
| `getByIid(String)` | 按连接 id 获取 | `super.selectOne(QueryParam.of("iid", iid))` |
| `modelClass()` | 返回模型类 | `MongoSSHConfig.class` |

- 调用链：`MongoConnectStore.replace → MongoSSHConfigStore.replace`

## MongoSettingStore

- 职责：MongoDB 设置存储，继承 `JdbcKeyValueStore<MongoSetting>`（键值式）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoSettingStore`（static final） | 单例 |
| `SETTING` | `MongoSetting`（static final） | 全局当前设置，由 `INSTANCE.load()` 初始化 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `load()` | 加载设置，失败返回默认值 | `super.select()`；异常时打印并 `JulLog.warn`；为空时 `new MongoSetting()` |
| `replace(MongoSetting)` | 替换设置 | 非空则 `update` |
| `modelClass()` | 返回模型类 | `MongoSetting.class` |

- 调用链：`EasyMongoApp.init → MongoSettingStore.SETTING → I18nManager/FontManager/ThemeManager.apply`

## ShellTerminalHistoryStore

- 职责：命令行终端历史存储，映射 `ShellTerminalHistory`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `ShellTerminalHistoryStore`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `replace(ShellTerminalHistory)` | 保存终端历史（始终新增） | 直接 `insert(model)` |
| `modelClass()` | 返回模型类 | `ShellTerminalHistory.class` |

- 调用链：`MongoTerminalHistoryHandler → ShellTerminalHistoryStore.replace → insert`
