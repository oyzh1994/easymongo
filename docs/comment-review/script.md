# script 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/script/`
>
> 说明：本包基于 Nashorn 脚本引擎，在脚本环境中模拟 mongo shell，向用户提供 `db`、`db.xxx` 等脚本级 API。

## DBDialect

- 职责：数据库类型（方言）枚举，当前仅定义 `MYSQL`。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `MYSQL` | 枚举常量 | MySQL 数据库方言 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `valueList()`（static） | 获取全部方言列表 | `values()` 收集为 `List<DBDialect>` |

- 调用链：`DBDialect.valueList() → 方言下拉选择`

## MongoScriptCollection

- 职责：脚本环境中的 MongoDB 集合操作封装，向脚本提供集合级增删改查、索引、聚合、去重、重命名等接口。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbName` | `String`（final） | 所属数据库名称（取自 `getNamespace()`） |
| `collectionName` | `String`（final） | 集合名称 |
| `collection` | `MongoCollection<Document>`（final） | 底层 MongoDB 集合对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `find()` / `find(doc)` | 查询文档 | `find(doc)` 用 `MongoScriptUtil.toDocumentOrDefault` 构造过滤条件，返回 `MongoScriptFindCursor` |
| `insert(doc)` | 插入文档（自动判单条/批量） | `ScriptObjectMirror` 数组或 `Collection` → `insertMany`；否则 `insertOne` |
| `insertOne(doc)` | 插入单条 | 转 `Document`，非空才 `collection.insertOne` |
| `insertMany(doc)` | 批量插入 | `ScriptObjectMirror` 递归取 `values()`；`Collection` 逐个转 `Document` 后 `insertMany` |
| `delete(doc)` / `deleteOne(doc)` | 删除一条 | 转 `Document` 过滤条件后 `deleteOne` |
| `deleteMany()` / `deleteMany(doc)` | 删除全部/条件删除 | 过滤条件用 `toDocumentOrDefault` 后 `deleteMany` |
| `update(filter, doc)` / `updateOne(filter, doc)` | 更新一条 | 过滤与更新内容均需成功转为 `Document` |
| `drop()` | 删除当前集合 | `collection.drop()` |
| `findOne()` / `findOne(filter)` | 查询首条文档 | `collection.find(filter).first()`；过滤条件为空则查全部 |
| `findOneAndDelete(filter)` | 查询并删除 | 过滤条件非空时 `findOneAndDelete` |
| `findOneAndReplace(filter, replacement)` | 查询并替换 | 过滤与替换内容均需转换成功 |
| `findOneAndUpdate(filter, update)` | 查询并更新 | 过滤与更新内容均需转换成功 |
| `updateMany(filter, update)` | 更新全部 | 同上 |
| `replaceOne(filter, replacement)` | 替换一条 | 基础重载 |
| `replaceOne(filter, replacement, option)` | 按选项替换 | `option` 为 `Map` 时用 `JSONUtil.toBean(o, ReplaceOptions.class)` 转换选项 |
| `countDocuments()` / `countDocuments(filter)` | 统计文档数 | 过滤条件非空时按条件统计 |
| `estimatedDocumentCount()` | 估算文档总数 | `collection.estimatedDocumentCount()` |
| `distinct(fieldName)` / `distinct(fieldName, filter)` | 字段去重值 | 以 `String.class` 返回，包装为 `MongoScriptCursor` |
| `aggregate(pipeline)` | 执行聚合管道 | `toDocumentList(pipeline)` 转阶段，`allowDiskUse(true)` |
| `createIndex(keys)` | 创建索引 | 委托 `createIndex(keys, null)` |
| `createIndex(keys, options)` | 按选项创建索引 | 解析 `name/unique/background/sparse/expireAfterSeconds` 生成 `IndexOptions` |
| `listIndexes()` | 列出全部索引 | 包装 `collection.listIndexes()` 为游标 |
| `dropIndex(keys)` / `dropIndexByName(name)` / `dropIndexes()` | 删除索引 | 分别按键、按名称、全部删除 |
| `rename(newName)` | 重命名集合 | `renameCollection(new MongoNamespace(dbName, newName))` |

- 调用链：
  - `脚本 db.coll.find({...}) → MongoScriptCollection.find → MongoScriptUtil.toDocumentOrDefault → MongoScriptFindCursor`
  - `脚本 db.coll.insert([...]) → insert → insertMany → MongoScriptUtil.toDocument → collection.insertMany`
  - `脚本 db.coll.aggregate([...]) → aggregate → allowDiskUse(true) → MongoScriptCursor`

## MongoScriptCursor

- 职责：MongoDB 脚本游标的通用封装，将底层可迭代结果转换为脚本可用形式。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `cursor` | `MongoIterable<?>`（final） | 底层 MongoDB 可迭代结果 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoScriptCursor(MongoIterable<?>)` | 构造游标封装 | 保存底层迭代器 |
| `pretty()` | 格式化为易读 JSON | 遍历收集到 `List` 后 `JSONUtil.toPretty(list)` |
| `toArray()` | 收集为列表 | 遍历 `cursor` 收集到 `List`（原按类型分支逻辑已注释，统一收集） |
| `toString()` | 字符串输出 | 返回 `pretty()` |

- 调用链：`脚本 print(cursor) → MongoScriptCursor.toString → pretty → JSONUtil.toPretty`

## MongoScriptDatabase

- 职责：脚本环境中的 MongoDB 数据库操作封装，提供库级集合管理、命令执行、聚合与变更流监听。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `database` | `MongoDatabase`（final） | 底层 MongoDB 数据库对象 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getName()` | 获取库名 | `database.getName()` |
| `getCollection(name)` | 获取集合封装 | `new MongoScriptCollection(database.getCollection(name))` |
| `createCollection(name)` | 创建集合 | `database.createCollection(name)` |
| `createCollection(name, options)` | 按选项创建集合 | 解析 `capped/sizeInBytes/maxDocuments` 生成 `CreateCollectionOptions` |
| `drop()` | 删除当前数据库 | `database.drop()` |
| `runCommand(command)` | 执行数据库命令 | `command` 为 `Map` 时转 `Document` 后 `runCommand`，否则返回 null |
| `createView(name, viewOn, pipeline)` | 创建视图 | `toDocumentList(pipeline)` 转阶段后 `createView` |
| `createView(name, viewOn, pipeline, options)` | 按选项创建视图 | 构造 `CreateViewOptions`（当前未解析 options 字段）后 `createView` |
| `aggregate(pipeline)` | 库级聚合 | `allowDiskUse(true)`，返回 `MongoScriptCursor` |
| `watch()` / `watch(pipeline)` | 变更流监听 | `database.watch()` / `watch(stages)`，包装为游标 |
| `listCollectionNames()` | 列出集合名称 | 包装 `listCollectionNames()` |
| `listCollections()` | 列出集合信息 | 包装 `listCollections()` |

- 调用链：
  - `MongoScriptEngine.db(dbName) → new MongoScriptDatabase(client.getDatabase(dbName))`
  - `脚本 db.getCollection("x") → MongoScriptCollection`

## MongoScriptEngine

- 职责：MongoDB 脚本引擎封装，基于 Nashorn 构建并注入 MongoDB 相关全局对象与函数。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `mongoClient` | `MongoClient`（final） | MongoDB 客户端 |
| `engine` | `ScriptEngine` | Nashorn 脚本引擎实例 |
| `bindings` | `Bindings` | 引擎作用域绑定 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoScriptEngine(MongoClient)` | 构造并初始化引擎 | 保存客户端后 `initEngine()` |
| `initEngine()` | 初始化引擎 | `NashornScriptEngineFactory.getScriptEngine("--language=es6", "-scripting")`；注入全局构造器 `Code`、`Long`、`Int32`、`Binary`、`ISODate`、`ObjectId`（对应 `script.function` 下的 Function 类） |
| `getEngine()` | 获取引擎实例 | 返回 `engine` |
| `db(dbName)` | 切换当前数据库 | `client.getDatabase(dbName)` 包装为 `MongoScriptDatabase`，注入绑定 `db` 与 `dbName` |
| `eval(script)` | 执行脚本 | `engine.eval(script)` |
| `put(key, val)` | 注入变量 | `engine.put(key, val)` |

- 调用链：
  - `MongoClient.shellEngine() → new MongoScriptEngine(client) → initEngine → 注入 Code/Long/...`
  - `MongoClient.eval(db, script) → engine.db(dbName) → eval(script)`

## MongoScriptFindCursor

- 职责：脚本环境中的查询游标封装，在通用游标基础上支持分页（limit/skip）与执行计划分析。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `dbName` | `String`（final） | 所属数据库名称 |
| `collectionName` | `String`（final） | 集合名称 |
| `cursor` | `FindIterable<Document>`（final） | 底层查询结果集 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoScriptFindCursor(dbName, collectionName, cursor)` | 构造 | 透传底层 `cursor` 给父类 |
| `getDbName()` / `getCollectionName()` | 获取库名/集合名 | 返回字段 |
| `limit(n)` | 限制返回数量 | 基于 `cursor.limit(n)` 返回新封装 |
| `skip(n)` | 跳过文档数量 | 基于 `cursor.skip(n)` 返回新封装 |
| `explain()` | 获取执行计划 | `cursor.explain()` |

- 调用链：`MongoScriptCollection.find → new MongoScriptFindCursor → limit/skip → cursor.limit/skip`

## MongoScriptParser

- 职责：MongoDB 脚本解析器，去除脚本注释并按分号拆分为可逐条执行的语句。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `scriptContent` | `String`（final） | 待解析的脚本内容 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoScriptParser(String)` | 构造 | 保存脚本内容 |
| `removeComment()` | 移除脚本注释 | 委托 `MongoUtil.removeComment(scriptContent)` |
| `parseScript()` | 拆分脚本为语句列表 | 先 `removeComment`；`show dbs`/`show collections` 直接整体返回；否则逐行扫描：遇 `db.` 开头置 `startFlag`，累积到以 `;` 结尾的行后作为一条语句收集；末尾残留内容作为最后一条 |
| `getParser(String)`（static） | 创建解析器 | `new MongoScriptParser(script)` |

- 调用链：`MongoQueryUtil/执行入口 → MongoScriptParser.getParser(script) → parseScript → MongoUtil.removeComment`

## MongoScriptUtil

- 职责：MongoDB 脚本工具类，提供脚本对象（Nashorn `ScriptObjectMirror`）到 BSON `Document` 的转换以及脚本可调用函数的收集。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `databaseFuncions()`（static） | 获取数据库封装暴露的函数名集合 | `functions(MongoScriptDatabase.class)` |
| `collectionFuncions()`（static） | 获取集合封装暴露的函数名集合 | `functions(MongoScriptCollection.class)` |
| `functions(Class)`（static） | 收集公开函数名 | 反射 `getMethods()`，排除 static/native/protected/private 及 `ReflectUtil.objectMethodNames()` 中的 Object 方法 |
| `toDocumentList(Object)`（static） | 脚本数组/集合 → `List<Document>` | `ScriptObjectMirror` 取 `values()`，`Collection` 直接遍历，逐个 `toDocument` |
| `toDocument(Object)`（static） | obj → `Document` | `ScriptObjectMirror` 数组返回 null，否则递归转换；`Map` 转 `Document`；其余 null |
| `toDocumentOrDefault(Object)`（static） | obj → `Document`（空时给空文档） | 转换失败返回 `new Document()`（用于 filter 默认值） |
| `convertValue(Object)`（static） | 递归转换值 | 数组 → `List`，对象 → `Document`，`Map` → `Document`，`Collection` → `List` |
| `toDocument(Map)`（static） | `Map` → `Document` | 逐项 `convertValue` 后 `put` |

- 调用链：
  - `MongoScriptCollection.find/insert/... → MongoScriptUtil.toDocument/toDocumentOrDefault`
  - `MongoTerminalCompleteHandler.findCommandHandlers → MongeScriptUtil.collectionFuncions/databaseFuncions`
