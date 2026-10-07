# util 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/util/`

## MongoConnectUtil

- 职责：MongoDB 连接工具类，提供连接测试与连接关闭能力。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `testConnect(StageAdapter view, MongoConnect dbInfo)`（static） | 异步测试连接 | `ThreadUtil.start` 中禁用页面、置等待光标、追加标题「连接测试中」；`new MongoClient(dbInfo).start()`；已连接则 `close()` 并 `MessageBox.okToast`，否则 `MessageBox.warn`；finally 恢复页面/光标/标题 |
| `close(MongoClient client, boolean async)`（static） | 关闭连接 | 客户端非空且已连接时，按 `async` 决定 `ThreadUtil.start(client::close)` 或直接执行 |

- 调用链：`连接配置页 → MongoConnectUtil.testConnect → new MongoClient(start/close)`

## MongoDataUtil

- 职责：MongoDB 数据工具类，负责把记录值转义、按 BSON 类型构建脚本字面量，并生成插入/更新/替换脚本。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `escapeQuotes(String)`（static） | 转义引号与特殊字符 | 对 `"`→`\"`、`\`→`\\`、`\r`→`\r`、`\n`→`\n`（单引号保持原样） |
| `getRecordScript(MongoRecord, boolean skipId)`（static） | 由记录生成 `{...}` 文档脚本 | 遍历列（`skipId` 时跳过 `_id`），`_id` 取 `property.getOriginal()`，其余取 `property.get()`，逐项 `buildRecordData` 拼接 |
| `buildRecordValue(Object, int deep)`（static） | 按类型构建值 | 由 `MongoUtil.getType` 推断类型后重载 |
| `buildRecordValue(Object, String type, int deep)`（static） | 按类型构建脚本字面量 | `int`→`Int32(...)`、`long`→`Long(...)`、`obejectid`→`ObjectId('..')`、`date`→`ISODate('..')`、`binary`→`Binary.createFromBase64('..', 0)`、`list` 递归成 `[...]`、`object` 递归成 `{...}`、其余加单引号 |
| `buildRecordData(MongoColumn, value, builder, deep)`（private static） | 构建「列名: 值」 | 缩进拼接，类型取自列 |
| `buildRecordData(String colName, value, builder, deep)`（private static） | 构建「键: 值」 | 类型由值推断（嵌套对象用） |
| `toInsertScript(MongoRecord)`（static） | 记录 → 插入脚本 | 取首列集合名后拼接 |
| `toInsertScript(String collectionName, String doc)`（static） | 生成 `db.getCollection(...).insert(...)` | 模板替换 |
| `toInsertScript(List<MongoRecord>)`（static） | 批量转插入脚本 | 逐条调用单记录版本 |
| `toUpdateScript(MongoRecord)`（static） | 记录 → 更新脚本 | 用 `_idColumn()`/`_idValue()`，跳过 `_id` 生成 `$set` 文档 |
| `toUpdateScript(String collectionName, Object id, String doc)`（static） | 生成 `update({_id:..},{$set:..})` | id 经 `buildRecordValue` 处理 |
| `toReplaceScript(MongoFunction)`（static） | 函数 → 替换脚本 | 生成对 `system.js` 的 `replaceOne(..., {upsert:true})`，替换 `$code` 为函数代码 |

- 调用链：`记录复制为脚本菜单 → MongoRecordUtil.getColumnMenuItem(vCopyAsInsertSql) → MongoDataUtil.toInsertScript → buildRecordValue`

## MongoI18nHelper

- 职责：MongoDB 国际化辅助类，集中提供资源包文案读取。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `welcome()`（static） | 首页欢迎语 | `I18nResourceBundle.i18nString("mongo.home.welcome")` |
| `tableTip2()` / `tableTip3()` / `tableTip4()`（static） | 表格提示文本 | 分别读 `db.table.tip2/3/4` |

- 调用链：`MongoTerminalPane.init → MongoI18nHelper.welcome()`

## MongoNodeUtil

- 职责：MongoDB 节点工具类，负责在编辑控件与值之间读写，并按字段类型生成对应编辑控件。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getNodeVal(Node)`（static） | 读取节点值 | 按控件类型取 `getValue()`（`NumberTextField`/`DecimalTextField`/`DateTimeTextField`/`BinaryTextFiled`/`BooleanTextFiled`/`CodeTextFiled`/`JsonTextFiled`），普通 `TextField` 取 `getText()` |
| `setNodeVal(Node, Object)`（static） | 设置节点值 | 按控件类型 `setValue`；`BinaryTextFiled` 对 `Binary` 取 `getData()`；普通 `TextField` 走 `setText` |
| `generateNode(MongoColumn)`（static） | 按字段类型生成编辑控件 | `supportInteger`→`NumberTextField`；`supportDigits`→`DecimalTextField`；`supportDate`→`DateTimeTextField`；`supportBinary`→`BinaryTextFiled`；`supportList`→`JsonTextFiled(array=true)`；`supportObject`→`JsonTextFiled`；`supportCode`→`CodeTextFiled`；`supportBoolean`→`BooleanTextFiled`；否则 `FXTextField`；统一 `setId("value")` |
| `setToNullString(Node)`（static） | 置为 Null 占位 | 清空文本并设提示为 `MongoRecordUtil.nullPromptText()`，取消焦点 |
| `setToEmptyString(Node)`（static） | 置为空字符串 | 清空文本与提示，取消焦点 |

- 调用链：`文档新增/编辑页 → MongoNodeUtil.generateNode(column) → getNodeVal/setNodeVal`

## MongoProcessUtil

- 职责：MongoDB 进程工具类，封装应用重启。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `restartApplication()`（static） | 重启应用 | `ProcessUtil.restartApplication(100, StageManager::exit)`，`IOException` 打印 |

- 调用链：`SettingController.saveSetting → MongoProcessUtil.restartApplication`

## MongoRecordUtil

- 职责：MongoDB 记录工具类，负责按字段类型生成记录编辑控件（含配色）、格式化值、构建右键菜单，以及文档与记录的互转。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getNode(property, object, column)`（static） | 按字段类型生成编辑控件 | 依 `supportInt32/Int64/Digits/Binary/Date/Boolean/List/Object/Code/ObjectId` 创建对应控件并设置不同背景色；对 `TextField` 空值设 Null 占位、绑定右键菜单、文本变更时 `property.setChanged(true)` |
| `formatValue(object, column)`（static） | 按字段类型格式化值 | 按类型调用 `NumberTextField/DecimalTextField/ClearableTextField/BinaryTextFiled/JsonTextFiled/BooleanTextFiled.format`，日期用 `MongoUtil.DATE_FORMAT` |
| `nullPromptText()`（static） | 空值占位文本 | 返回 `"(Null)"` |
| `suitableColumnWidth(MongoColumn)`（static） | 计算合适列宽 | `_id` 列按 40 字符宽度；否则取字段名与类型宽度较大者 + 50 |
| `getColumnMenuItem(MongoRecordProperty)`（static） | 构建字段右键菜单 | 复制、粘贴、置 Null、置空串、复制为插入脚本、复制为更新脚本 |
| `docToRecord(String doc, dbName, collectionName)`（static） | JSON 字符串文档 → 记录 | `JSONUtil.parseObject` 后逐键建列、`putValue` 并设类型 |
| `isCollection(String name)`（static） | 是否集合 | 不以 `.files`/`.chunks` 结尾且非 `system.js` |
| `isBucket(String name)`（static） | 是否存储桶 | 以 `.files` 结尾 |
| `idValue(Object value)`（static） | 解析 `_id` 值 | 对 `ObjectId` 取十六进制；对各类 `BsonValue`（String/ObjectId/DbPointer/Binary/Boolean/Null/Double/Int32/Int64/Decimal128/Timestamp/DateTime）解包；其余 `toString` |
| `columns(List<MongoRecord>)`（static） | 汇总记录列 | 遍历所有记录，首次出现的列加入结果 |
| `docToRecord(dbName, collectionName, FindIterable<Document>)`（static） | 迭代器 → 记录列表 | 逐文档调用单文档版本 |
| `docToRecord(dbName, collectionName, Document)`（static） | 文档 → 记录 | 逐键建 `MongoColumn`（设库名/集合名/类型）并 `putValue`；空文档返回 null |

- 调用链：
  - `记录表格 → MongoRecordUtil.getNode → MongoNodeUtil/各编辑控件`
  - `查询结果解析 → MongoQueryResult.parseResult → MongoRecordUtil.columns`
  - `复制为插入脚本 → getColumnMenuItem → MongoDataUtil.toInsertScript`

## MongoUtil

- 职责：MongoDB 通用工具类，提供常量、类型推断、脚本注释移除与克隆名生成。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `ID` | `String`（static final） | 主键字段名 `_id` |
| `SYSTEM_JS` | `String`（static final） | 系统脚本集合名 `system.js` |
| `DATE_FORMAT` | `SimpleDateFormat`（static final） | 日期格式 `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getType(Object)`（static） | 推断值的类型名 | 依 Java 类型与 `BsonValue` 子类型返回 `int/long/double/string/list/boolean/date/binary/obejectid/object/code`（原样保留 typo `obejectid`） |
| `isPrimaryType(Object)`（static） | 是否原始类型 | 类型属于 int/long/double/list/object/boolean |
| `removeComment(String sql)`（static） | 移除脚本注释 | 逐行剔除 `-- `/`#`/`//` 单行注释与 `/* */` 多行注释，保留非空正常行 |
| `genCloneName()`（static） | 生成克隆名称 | `"_clone_"` + UUID 前 5 位 |

- 调用链：`MongoScriptParser.removeComment → MongoUtil.removeComment`；`MongoDataUtil.buildRecordValue → MongoUtil.getType`

## MongoViewFactory

- 职责：mongo 页面工厂，统一创建并展示各类文档/数据相关弹窗页面。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `documentAdd(MongoColumns)`（static） | 打开新增文档页 | `parseStage(MongoCollectionDocumentAddController)`，设 `columns` 属性后 `showAndWait` |
| `documentUpdate(MongoRecord)`（static） | 打开编辑文档页 | `MongoCollectionDocumentUpdateController`，设 `document` 属性 |
| `bucketDocumentUpdate(MongoRecord)`（static） | 打开存储桶文档编辑页 | `MongoBucketDocumentUpdateController` |
| `databaseAdd(MongoConnectTreeItem)`（static） | 打开新增数据库页 | `MongoDatabaseAddController`，设 `connectItem` 属性 |
| `exportData(client, collectionName, tableName)`（static） | 导出数据（简化重载） | 委托 5 参重载 |
| `exportData(client, dbName, collectionName, exportMode, exportCollection)`（static） | 打开导出页 | `ShellMongoDataExportController`，设置 `dbName/dbClient/collectionName/exportMode/exportTable` |
| `importData(client, dbName)`（static） | 打开导入页 | `ShellMongoDataImportController`，设置 `dbName/dbClient` |
| `dumpData(client, dbName, tableName, dumpType)`（static） | 打开转储页 | `ShellMongoDataDumpController`，设置 `dumpType/dbName/dbClient/tableName` |
| `runScriptFile(client, dbName)`（static） | 打开运行脚本文件页 | `ShellMongoRunScriptFileController`，设置 `dbName/dbClient` |
| `fileView(file, client, type)`（static） | 打开文件查看页 | `MongoBucketDocumentViewController`，设置 `file/type/client` |

- 调用链：`记录菜单/工具栏 → MongoViewFactory.documentUpdate → MongoCollectionDocumentUpdateController`

## ShellFileUtil

- 职责：文件工具类，按扩展名判断文件可查看类型并生成临时文件路径。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `fileViewable(String extName)`（static） | 判断可查看类型 | 图片扩展名→`img`；视频→`video`；音频→`audio`；文本类扩展名→`txt`；其余→`unknown` |
| `getTempFile(String extName)`（static） | 生成临时文件路径 | `MongoConst.getCachePath()` + UUID + `.` + 扩展名 |

- 调用链：`文件查看 → ShellFileUtil.fileViewable → MongoViewFactory.fileView`

## ShellMongoConnectUtil

- 职责：MongoDB shell 连接工具类，解析 shell 连接命令并写回连接对象。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `parse(String input)`（static） | 解析连接命令 | 空格分词，识别 `-server ip:port`（拆 host/port）、`-timeout 毫秒`（转秒）、`-r`（只读），返回 `ShellMongoConnectInfo`；异常返回 null |
| `copyConnect(ShellMongoConnectInfo, MongoConnect)`（static） | 复制连接信息 | 写入只读、超时，并把 `host + ":" + port` 组合为连接地址 |

- 调用链：`MongoTerminalPane.connect → ShellMongoConnectUtil.parse → copyConnect → client.start`
