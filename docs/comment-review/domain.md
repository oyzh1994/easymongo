# domain 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/domain/`

## MongoConnect

- 职责：MongoDB 连接信息实体，映射数据库表 `t_connect`，同时实现 `Comparable`（按名称排序）与 `ObjectComparator`（按名称判等）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | `String` | 数据 id（`@Column @PrimaryKey`） |
| `host` | `String` | 连接地址（`host:port` 形式） |
| `name` | `String` | 连接名称 |
| `user` | `String` | 认证用户 |
| `type` | `String` | 类型 |
| `authType` | `String` | 认证方式 |
| `authDatabase` | `String` | 认证数据库 |
| `password` | `String` | 认证密码 |
| `remark` | `String` | 备注信息 |
| `readonly` | `Boolean` | 只读模式 |
| `groupId` | `String` | 分组 id |
| `collects` | `List<String>` | 收藏路径列表（非持久化字段） |
| `connectTimeOut` | `Integer` | 连接超时时间（秒） |
| `sshForward` | `Boolean` | 是否开启 SSH 转发 |
| `sshConfig` | `MongoSSHConfig` | SSH 配置（非持久化字段） |
| `sid` | `String` | 服务标识 |
| `serviceName` | `String` | 服务名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `copy(MongoConnect)` | 复制连接字段到当前对象 | 逐字段赋值，返回 `this` |
| `isSSHForward()` | 是否开启 SSH 转发 | `BooleanUtil.isTrue(sshForward)` |
| `isReadonly()` | 是否只读模式 | `BooleanUtil.isTrue(readonly)` |
| `isCollect(path)` | 是否已收藏 | 收藏列表非空且包含路径 |
| `addCollect(path)` | 添加收藏 | 懒初始化 `collects`，去重后加入 |
| `removeCollect(path)` | 取消收藏 | `collects.remove(path)` |
| `getConnectTimeOut()` | 获取连接超时（秒） | 空或 <1 时返回默认 5 |
| `connectTimeOutMs()` | 连接超时毫秒值 | `getConnectTimeOut() * 1000` |
| `compareTo(MongoConnect)` | 按名称忽略大小写排序 | `name.compareToIgnoreCase` |
| `hostIp()` | 提取连接 ip | `host` 含 `:` 时取前段，否则空串 |
| `hostPort()` | 提取连接端口 | 无逗号且含 `:` 时解析端口，否则 -1 |
| `compare(MongoConnect)` | 按名称判等 | `StringUtil.equals(name, t1.name)` |
| `serviceName()` | 获取服务名称 | 优先返回 `sid`，否则 `serviceName` |
| `checkServiceType()` | 检查服务类型 | 无 `sid` 返回 `"sid"`，否则 `"serviceName"` |
| `isPasswordAuth()` | 是否密码认证 | `"password".equalsIgnoreCase(authType)` |
| 其余 `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：
  - `MongoConnectStore.replace(conn) → conn.getSshConfig → MongoSSHConfigStore.replace`
  - `MongoConnectUtil → conn.connectTimeOutMs() → MongoClient`（连接超时时间传递）

## MongoGroup

- 职责：MongoDB 连接分组实体，映射表 `t_group`，继承 `AppGroup` 并按名称判等。

- 字段：（继承 `AppGroup`，自带 `name`、`gid`、`expand` 等）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoGroup()` / `MongoGroup(name, groupId, expand)` | 构造分组 | 委托 `AppGroup` 构造 |
| `compare(MongoGroup)` | 分组判等 | 按名称 `StringUtil.equals` 比较 |

- 调用链：`MongoGroupStore.replace(group) → group.getGid/getName`

## MongoQuery

- 职责：db 查询实体，映射表 `t_query`，承载查询名称与内容。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `uid` | `String` | 数据 id（`@Column @PrimaryKey`） |
| `iid` | `String` | 连接 id |
| `dbName` | `String` | 数据库名称 |
| `name` | `String` | 名称 |
| `content` | `String` | 内容（查询语句） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `copy(MongoQuery)` | 复制字段 | 复制 `iid/name/dbName/content`，返回 `this` |
| `compareTo(MongoQuery)` | 排序 | `StringUtil.compare(t1.uid, this.uid, true)` |
| `compare(MongoQuery)` | 判等 | 按 `uid` 比较 |
| `isNew()` | 是否新建查询 | `uid == null` |
| `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`MongoQueryStore.list(iid, dbName) → MongoQuery`；`MongoQueryMainTab.init(query, …) → MongoQuery`

## MongoSSHConfig

- 职责：MongoDB 连接的 SSH 配置，映射表 `t_ssh_config`，继承通用 `SSHConnect`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `iid` | `String` | 关联连接 id（`@Column @PrimaryKey`，指向 `MongoConnect`） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getIid/setIid` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`MongoConnectStore.replace → setIid(conn.getId()) → MongoSSHConfigStore.replace`

## MongoSetting

- 职责：db 设置实体，映射表 `t_setting`，继承 `AppSetting`（含 locale/字体/主题/透明度等配置）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `recordPageLimit` | `Integer` | 记录每页限制 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `setRecordPageLimit(Integer)` | 设置每页限制 | 空或 <=0 时置 100 |
| `getRecordPageLimit()` | 获取每页限制 | 空或 <=0 时返回 100 |

- 调用链：`MongoSettingStore.SETTING → MongoPageSettingPopupController.apply → setRecordPageLimit`

## ShellTerminalHistory

- 职责：命令行终端历史记录实体，映射表 `t_terminal_history`，继承通用 `TerminalHistory`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `tid` | `String` | 数据 id（`@Column @PrimaryKey`） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getTid/setTid` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`ShellTerminalHistoryStore.replace → insert(history)`
