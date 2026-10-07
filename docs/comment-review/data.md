# easymongo `data` 包代码审查文档

`cn.oyzh.easymongo.data` 及其子包（`config`、`dto`、`file`、`handler`、`ui`）实现了 MongoDB 数据的导入、导出、转储、传输与脚本文件执行能力，包含配置对象、DTO、文件读写器、处理器与配套 UI 控件。

说明：`MysqlCsvTypeFileReader`、`MysqlTxtTypeFileReader` 整文件被注释，属死代码，不在本文档覆盖范围。

覆盖类/接口/枚举数：43

---

## MongoDataImportHelper
- 职责：数据导入值解析工具，将文本值转为数字/JSON 数组等类型。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| parseValue(String) | 解析值 | 小数→`Double`，整数→`Integer`，`[...]`→`JSONUtil.parseArray`，否则原样返回 |

- 调用链：`MongoXmlTypeFileReader.readObject → MongoDataImportHelper.parseValue`

---

## MongoDataExportConfig
- 职责：MongoDB 数据导出配置。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dateFormat | String | 日期格式 |
| fieldToAttr | boolean | 字段作为属性 |
| includeFields | boolean | 包含列标题（默认 true） |
| recordSeparator | String | 记录分割符号（默认系统换行） |
| fieldSeparator | String | 字段分割符号（默认 ";"） |
| txtIdentifier | String | 文本识别符号（默认 `"`） |
| charset | String | 字符集（默认 UTF-8） |
| earlyVersion | boolean | 早期版本格式 |

- 方法：各字段的 getter/setter（`isFieldToAttr`、`isIncludeFields`、`isEarlyVersion` 等）。

- 调用链：`DBDataExportHandler → new MongoDataExportConfig → 各 MongoXxxTypeFileWriter`

---

## MongoDataImportConfig
- 职责：MongoDB 数据导入配置。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dateFormat | String | 日期格式 |
| importMode | String | 导入模式（1 追加/2 复制，默认 "2"） |
| columnIndex | int | 字段索引（默认 0） |
| dataStartIndex | int | 数据起始索引（默认 1） |
| recordLabel | String | 字段标签 |
| attrToColumn | boolean | 属性作为字段 |
| recordSeparator | String | 记录分割符号 |
| fieldSeparator | String | 字段分割符号（默认 ";"） |
| txtIdentifier | String | 文本识别符号 |
| charset | String | 字符集 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| isAppendMode()/isCopyMode() | 模式判定 | importMode 比较 "1"/"2" |
| fieldSeparatorChar()/txtIdentifierChar() | 取首个字符 | 分隔符/识别符首字符 |
| 各字段 getter/setter | 读写 | — |

- 调用链：`DBDataImportHandler → new MongoDataImportConfig → 各 MongoXxxTypeFileReader`

---

## ShellMongoDataExportCollection
- 职责：数据导出集合 DTO，承载集合名称、记录、字段与文件路径/选中/扩展属性。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 表名称 |
| records | List<MongoRecord> | 记录列表（查询导出用） |
| columns | List<ShellMongoDataExportColumn> | 字段列表 |
| filePathProperty | StringProperty | 文件路径属性 |
| selectedProperty | BooleanProperty | 是否选中属性 |
| extensionProperty | ObjectProperty<FileExtensionFilter> | 扩展后缀属性 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| selectedProperty()/isSelected()/setSelected(...) | 选中读写 | 选中时无路径则 `updateFilePath` |
| getSelectedControl() | 选中控件 | 返回 `FXCheckBox` 并双向同步 |
| filePathProperty()/getFilePath()/setFilePath(...) | 文件路径读写 | — |
| getFilePathControl() | 文件路径控件 | 返回 `SaveFileTextField` |
| extensionProperty()/getExtension()/setExtension(...) | 扩展后缀读写 | 变化时 `updateFilePath` |
| fileName() | 默认文件名 | `name + extension` |
| columns(List<? extends MongoColumn>) | 设置字段列表 | 转为 `ShellMongoDataExportColumn` |
| columns()/getColumns()/setColumns(...) | 获取/设置字段 | — |
| selectedColumns()/selectedColumnNames() | 已选字段/名称 | 过滤 `isSelected` |
| hasColumns() | 是否含字段 | columns 非空 |
| updateFilePath() | 更新文件路径 | 桌面目录 + 文件名 |
| getName/setName/getRecords/setRecords | 读写 | — |

- 调用链：`DBDataExportHandler.exportTable → table.selectedColumns() → new MongoColumns`

---

## ShellMongoDataExportColumn
- 职责：数据导出字段 DTO（继承 `MongoColumn`），带选中标记。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| selected | boolean | 是否选中（默认 true） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| isSelected()/setSelected(...) | 选中读写 | — |

- 调用链：`ShellMongoDataExportCollection.columns(...) → new ShellMongoDataExportColumn`

---

## ShellMongoDataImportFile
- 职责：数据导入文件 DTO，承载待导入文件与目标表。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 数据库名称 |
| dbClient | MongoClient | 数据库客户端 |
| fileProperty | ObjectProperty<File> | 文件路径属性 |
| targetTableName | String | 目标表名称 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| setDbName(...)/setDbClient(...) | 设置库名/客户端 | — |
| fileProperty()/getFile()/setFile(...) | 文件属性读写 | — |
| getFilePath()/getFileName() | 路径/文件名 | 由 File 派生 |
| getFilePathControl() | 文件路径控件 | 返回 `ChooseFileTextField` |
| getTargetTableControl() | 目标表控件 | 返回 `ShellMongoCollectionComboBox` 并 init |
| getTableName() | 由文件名去扩展名 | 截取到最后一个 `.` |
| getTargetTableName()/setTargetTableName(...) | 目标表读写 | 默认取 `getTableName()` |

- 调用链：`DBDataImportHandler.importRecord → file.getTargetTableName()`

---

## ShellMongoDataTransportCollection
- 职责：数据传输集合 DTO。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 表名称 |
| selected | boolean | 是否选中（默认 true） |

- 方法：`getName/setName/isSelected/setSelected`。

- 调用链：`ShellMongoDataTransportTableListView.of → new ShellMongoDataTransportCollection`

---

## ShellMongoDataTransportFunction
- 职责：数据传输函数 DTO。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| name | String | 函数名称 |
| selected | boolean | 是否选中（默认 true） |

- 方法：`getName/setName/isSelected/setSelected`。

- 调用链：`ShellMongoDataTransportFunctionListView.of → new ShellMongoDataTransportFunction`

---

## MongoTypeFileReader
- 职责：数据导入文件读取器抽象基类。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| file | File | 待读取的文件 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoTypeFileReader(File) | 构造 | 记录 file |
| getFile() | 获取文件 | — |
| init() | 初始化（可重写） | 空实现 |
| readObject()（抽象） | 读取一个对象 | — |
| readObjects(int count) | 读取指定数量 | 循环 `readObject` 直到 count 或 null |
| parseLine(line, txtIdentifier, fieldSeparator) | 解析单行 | 状态机按文本标识符/分隔符切分 |

- 调用链：`DBDataImportHandler → MongoTypeFileReader 子类 → readObjects`

---

## MongoTypeFileWriter
- 职责：数据导出文件写入器抽象基类。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| init() | 初始化 | 空实现 |
| parameterized(MongoColumn, value, config) | 参数化值 | 按类型转字符串/日期/ObjectId 十六进制/二进制 hex/JSON |
| writeHeader()/writeTrial() | 写头/尾 | 默认空实现 |
| writeObject(Map)（抽象） | 写对象 | — |
| writeObjects(List<Map>) | 写多个对象 | 遍历 `writeObject` |
| formatLine(Object[], ...)/formatLine(List, ...) | 格式化行 | 字段用分隔符+文本标识符包裹 |

- 调用链：`DBDataExportHandler.exportTable → MongoTypeFileWriter 子类 → writeObjects`

---

## MongoCsvTypeFileWriter
- 职责：CSV 类型导出写入器。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| columns | MongoColumns | 字段列表 |
| config | MongoDataExportConfig | 导出配置 |
| writer | LineFileWriter | 文件写入器 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoCsvTypeFileWriter(filePath, config, columns) | 构造 | `LineFileWriter.create` |
| writeHeader() | 写列名 | `formatLine(columnNames, ",", ...)` |
| writeObject(Map) | 写一行 | 按列索引取值 `parameterized` 后成行 |
| close() | 关闭 | 关闭 writer 并清引用 |

- 调用链：`DBDataExportHandler.initWriter → new MongoCsvTypeFileWriter`

---

## MongoExcelTypeFileReader
- 职责：Excel 类型导入读取器。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| workbook | Workbook | Excel 工作簿 |
| columns | List<String> | 字段列表 |
| config | MongoDataImportConfig | 导入配置 |
| currentRowIndex | Integer | 当前行索引 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoExcelTypeFileReader(file, config) | 构造 | 依扩展名创建 Workbook 并 `init` |
| init() | 初始化 | 读取表头行作为列名，定位数据起始行 |
| readObject() | 读取一行 | 按单元格类型取值映射到列名 |
| close() | 关闭 | 关闭 workbook |

- 调用链：`DBDataImportHandler.initReader → new MongoExcelTypeFileReader`

---

## MongoExcelTypeFileWriter
- 职责：Excel 类型导出写入器。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| columns | MongoColumns | 字段列表 |
| config | MongoDataExportConfig | 导出配置 |
| workbook | Workbook | Excel 工作簿 |
| xlsRowIndex | int | 行记录索引 |
| filePath | String | 导出文件路径 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| MongoExcelTypeFileWriter(filePath, config, columns) | 构造 | 依扩展名创建 Workbook |
| writeHeader() | 写表头 | 建 Sheet 与列名行并落盘 |
| writeObject(Map, boolean) | 写一行 | 按类型写单元格，flush 控制落盘 |
| writeObject(Map)/writeObjects(List) | 写对象（批量一次落盘） | — |
| close() | 关闭 | 关闭 workbook |
| parameterized(...) | 参数化（覆盖） | null 返回 null |

- 调用链：`DBDataExportHandler.initWriter → new MongoExcelTypeFileWriter`

---

## MongoHtmlTypeFileWriter
- 职责：HTML 类型导出写入器。

- 字段：`columns`、`config`、`writer`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 `LineFileWriter` | — |
| writeHeader() | 写 HTML 头与表头 | 拼接 `<table>` 与 `<th>` |
| writeTrial() | 写 HTML 尾 | 拼接 `</table></body></html>` |
| writeObject(Map) | 写一行 | 拼接 `<tr><td>...` |
| close() | 关闭 | 关闭 writer |

- 调用链：`DBDataExportHandler.initWriter → new MongoHtmlTypeFileWriter`

---

## MongoJsTypeFileWriter
- 职责：JS 脚本类型导出写入器，输出 insert 脚本。

- 字段：`columns`、`config`、`writer`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 `LineFileWriter` | — |
| writeObject(Map) | 写脚本 | 组装 `MongoRecord` → `MongoDataUtil.toInsertScript` |
| close() | 关闭 | 关闭 writer |
| parameterized(...) | 参数化（覆盖） | null 返回 null |

- 调用链：`DBDataExportHandler.initWriter → new MongoJsTypeFileWriter → MongoDataUtil.toInsertScript`

---

## MongoJsonTypeFileReader
- 职责：JSON 类型导入读取器。

- 字段：`reader`（fastjson2 `JSONReader`）、`config`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 JSONReader 并 `init` | — |
| init() | 定位数组起点 | 支持纯数组或对象包装（recordLabel） |
| readObject() | 读取一个对象 | `startArray` 语义后 `readObject`，消费逗号 |
| close() | 关闭 | 消费包装结束符并关闭 |

- 调用链：`DBDataImportHandler.initReader → new MongoJsonTypeFileReader`

---

## MongoJsonTypeFileWriter
- 职责：JSON 类型导出写入器。

- 字段：`columns`、`config`、`writer`、`firstWrite`（是否首次写入）、`firstWrite`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 `LineFileWriter` | — |
| writeHeader() | 写 JSON 头 | 早期版本写 `{"RECORDS":[`，否则 `[` |
| writeTrial() | 写 JSON 尾 | `]` 或 `]}` |
| writeObject(Map) | 写一条 | 拼接字段，基础类型不加引号 |
| close() | 关闭 | 关闭 writer |
| parameterized(...) | 参数化（覆盖） | null 返回 null |

- 调用链：`DBDataExportHandler.initWriter → new MongoJsonTypeFileWriter`

---

## MongoTxtTypeFileWriter
- 职责：TXT 类型导出写入器。

- 字段：`columns`、`config`、`writer`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 `LineFileWriter` | — |
| writeHeader() | 写列名行 | `formatLine` |
| writeObject(Map) | 写一行 | 按列索引 `parameterized` 后成行 |
| close() | 关闭 | 关闭 writer |

- 调用链：`DBDataExportHandler.initWriter → new MongoTxtTypeFileWriter`

---

## MongoXmlTypeFileReader
- 职责：XML 类型导入读取器。

- 字段：`reader`（`XMLEventReader`）、`config`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 XMLEventReader 并 `init` | — |
| init() | 定位首个开始元素 | — |
| readObject() | 读取一个 RECORD | 依 `attrToColumn` 读属性或子节点，`MongoDataImportHelper.parseValue` |
| close() | 关闭 | 关闭 reader |

- 调用链：`DBDataImportHandler.initReader → new MongoXmlTypeFileReader`

---

## MongoXmlTypeFileWriter
- 职责：XML 类型导出写入器。

- 字段：`columns`、`config`、`writer`。

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 创建 `LineFileWriter` | — |
| writeHeader()/writeTrial() | 写 `<RECORDS>` 头尾 | — |
| writeObject(Map) | 写一条 | `fieldToAttr` 时属性形式，否则子节点形式 |
| close() | 关闭 | `IOUtil.close` |
| parameterized(...) | 参数化（覆盖） | null 返回 null |

- 调用链：`DBDataExportHandler.initWriter → new MongoXmlTypeFileWriter`

---

## DBDataHandler
- 职责：数据库数据处理基类，提供中断控制、消息与进度回调。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| interrupt | AtomicBoolean | 中断标志位 |
| messageHandler | Consumer<String> | 消息处理 |
| processedHandler | Consumer<Integer> | 进度处理 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| interrupt(boolean)/interrupt() | 设置中断 | 惰性建/更新 AtomicBoolean |
| checkInterrupt() | 检查中断 | 已中断则抛 `InterruptedException` |
| exception(Exception) | 发送异常 | 中断异常直接抛，否则回调 messageHandler |
| message(String) | 发送消息 | 回调 messageHandler |
| processed(int) | 更新进度 | 回调 processedHandler |
| processedIncr()/processedIncr(int) | 递增进度 | 取绝对值后 processed |
| processedDecr()/processedDecr(int) | 递减进度 | 处理符号后 processed |
| getInterrupt/setInterrupt/getMessageHandler/setMessageHandler/getProcessedHandler/setProcessedHandler | 读写 | 部分返回自身支持链式 |

- 调用链：`DBDataExportHandler/DBDataImportHandler/... extends DBDataHandler`

---

## DBDataDumpHandler
- 职责：数据转储处理器基类。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dataType | Byte | 数据类型（0 数据和结构/1 仅结构） |
| dbName | String | 库名称 |
| dumpFile | File | 转储文件 |
| fileWriter | FastFileWriter | 文件写入器 |
| dumpType | Byte | 1 库/2 集合 |
| tableName | String | 表名称 |
| dbInfo | MongoConnect | 连接信息 |
| queryLimit | int | 查询限制（默认 500） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| DBDataDumpHandler(dbName) | 构造 | 记录库名 |
| dumpFile(File) | 设置转储文件 | 关闭旧 writer 并新建 `FastFileWriter` |
| doDump()（抽象） | 执行转储 | — |
| writeHeader()（抽象）/writeTail() | 写头/尾 | 尾默认关闭 writer |
| isDumpRecord() | 是否转储数据 | dataType==0 |
| newHandler(MongoClient, dbName) | 工厂 | `new ShellMongoDataDumpHandler` |
| 各字段 getter/setter | 读写 | setDumpType/setTableName/setDbInfo/setQueryLimit 返回自身 |

- 调用链：`MongoViewFactory.dumpData → DBDataDumpHandler.newHandler → ShellMongoDataDumpHandler`

---

## DBDataExportHandler
- 职责：数据导出处理器基类，按文件类型选择写入器并分页导出集合。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 库名称 |
| fileType | String | 文件类型（sql/json 等） |
| dbClient | MongoClient | db 客户端 |
| queryLimit | int | 查询限制（默认 500） |
| tables | List<ShellMongoDataExportCollection> | 导出表 |
| config | MongoDataExportConfig | 导出配置（final） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 记录 dbClient/dbName，建 config | — |
| isSqlType/isXmlType/isCsvType/isHtmlType/isXlsType/isXlsxType/isExcelType/isJsonType/isTxtType/isJsType | 类型判定 | fileType 比较 |
| doExport() | 执行导出 | 遍历 tables，`checkInterrupt` + `exportTable` + `processedIncr` |
| initWriter(filePath, columns) | 初始化写入器 | 依类型返回对应 `MongoXxxTypeFileWriter` |
| exportTable(table) | 导出单表 | 分页查询 `selectCollectionRecords`，`writeRecord` 写文件 |
| writeHeader/writeRecord/writeTail | 写头/记录/尾 | 委托 writer |
| dateFormat/recordSeparator/txtIdentifier/fieldSeparator/includeFields/fieldToAttr/earlyVersion | 配置设置 | 写入 config |
| 各字段 getter/setter | 读写 | — |

- 调用链：`MongoViewFactory.exportData → new ShellMongoDataExportHandler → doExport → exportTable → MongoTypeFileWriter`

---

## DBDataImportHandler
- 职责：数据导入处理器基类，分页读取文件并批量插入集合。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 库名称 |
| fileType | String | 文件类型 |
| dbClient | MongoClient | db 客户端 |
| readLimit | int | 读取限制（默认 500） |
| batchLimit | int | 批量处理限制（默认 50） |
| files | List<ShellMongoDataImportFile> | 导入文件 |
| config | MongoDataImportConfig | 导入配置（final） |
| insertList | List<MongoRecord> | 待批量插入记录列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 记录 dbClient/dbName，建 config | — |
| isXmlType/isCsvType/isExcelType/isJsonType/isTxtType | 类型判定 | fileType 比较 |
| doImport() | 执行导入 | 遍历 files，`importRecord` |
| importRecord(file) | 导入单文件 | 复制模式先 `clearCollection`；读到空则 `doBatchInsert` 收尾 |
| initReader(file) | 初始化读取器 | 依类型返回对应 `MongoXxxTypeFileReader` |
| readRecords(reader, count) | 读取记录 | 从 Map 构造 `MongoColumns`/`MongoRecord` |
| writeRecord(records) | 累积记录 | 达 batchLimit 触发 `doBatchInsert` |
| doBatchInsert() | 执行批量插入 | 超批量则拆分多线程 `ThreadUtil.submitVirtual` |
| doBatchInsert(List) | 批量插入 | 设置 dbName 后 `insertCollectionRecord`，更新进度 |
| dateFormat/importMode/columnIndex/dataStartIndex/recordLabel/attrToColumn/recordSeparator/txtIdentifier/fieldSeparator | 配置设置 | 写入 config |
| 各字段 getter/setter | 读写 | — |

- 调用链：`MongoViewFactory.importData → new ShellMongoDataImportHandler → doImport → importRecord → MongoClient.insertCollectionRecord`

---

## DBDataRunFileHandler
- 职责：脚本文件执行处理器基类。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbName | String | 库名称 |
| sqlFile | File | sql 文件 |
| dbClient | MongoClient | db 客户端 |
| dbInfo | MongoConnect | 连接信息 |
| insertLimit | int | 插入限制（默认 500） |
| batchLimit | int | 批量限制（默认 50） |
| continueWithErrors | boolean | 遇错继续（默认 true） |
| engine | MongoScriptEngine | 脚本引擎 |
| insertList | List<String> | 待批量执行的插入 sql 列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造 | 记录 dbClient/dbName，初始化 engine 并 `engine.db(dbName)` | — |
| sqlFile(File) | 设置 sql 文件 | 返回自身 |
| runFile()（抽象） | 运行文件 | — |
| addInsertSql(String) | 累积插入 sql | 达 insertLimit 触发 `doBatchInsert` |
| doBatchInsert() | 执行批量插入 | 超批量则拆分 `ThreadUtil.submit`，异常经 AtomicReference 汇总 |
| doBatchInsert(List<String>) | 批量执行 | 逐条 `engine.eval`，统计成功数更新进度 |
| newHandler(MongoClient, dbName) | 工厂 | `new ShellMongoDataRunFileHandler` |
| 各字段 getter/setter | 读写 | setDbInfo/sqlFile 返回自身 |

- 调用链：`MongoViewFactory.runScriptFile → DBDataRunFileHandler.newHandler → ShellMongoDataRunFileHandler.runFile`

---

## DBDataTransportHandler
- 职责：数据传输处理器基类。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| sourceDatabase | String | 来源库 |
| targetDatabase | String | 目标库 |
| selectLimit | int | 查询限制（默认 500） |
| batchLimit | int | 批量限制（默认 50） |
| insertList | List<MongoRecord> | 待批量插入记录列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| doTransport()（抽象） | 执行传输 | — |
| addInsertSql(List<MongoRecord>) | 累积记录 | 达 batchLimit 触发 `doBatchInsert` |
| doBatchInsert() | 执行批量插入 | 超批量则拆分 `ThreadUtil.submit` |
| doBatchInsert(List<MongoRecord>)（抽象） | 批量插入 | — |
| 各字段 getter/setter | 读写 | — |

- 调用链：`数据传输入口 → ShellMongoDataTransportHandler.doTransport`

---

## ShellMongoDataDumpHandler
- 职责：MongoDB 数据转储处理器，将集合结构与数据转储为脚本。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| dbClient | MongoClient | db 客户端 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoDataDumpHandler(dbClient, dbName) | 构造 | — |
| doDump() | 执行转储 | dumpType==1 转全部集合与函数；==2 转单集合 |
| dumpCollection() | 转全部集合 | 遍历 `listCollections` 逐个转储 |
| dumpCollection(MongoCollection) | 转单集合 | 写 drop/create 语句，按需转数据 |
| dumpRecord(tableName) | 转数据 | 分页 `selectCollectionRecords` → `MongoDataUtil.toInsertScript` |
| dumpFunction() | 转函数 | 生成 system.js 的 deleteOne/insert 脚本 |
| writeHeader() | 写文件头 | 含版本、主机、库、时间等注释信息 |
| getDbClient/setDbClient | 读写 | — |

- 调用链：`DBDataDumpHandler.newHandler → ShellMongoDataDumpHandler.doDump → dumpCollection/dumpRecord`

---

## ShellMongoDataExportHandler
- 职责：MongoDB 数据导出处理器（继承基类，无额外逻辑）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoDataExportHandler(dbClient, dbName) | 构造 | `super(dbClient, dbName)` |

- 调用链：`MongoViewFactory.exportData → new ShellMongoDataExportHandler`

---

## ShellMongoDataImportHandler
- 职责：MongoDB 数据导入处理器（继承基类，无额外逻辑）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoDataImportHandler(dbClient, dbName) | 构造 | `super(dbClient, dbName)` |

- 调用链：`MongoViewFactory.importData → new ShellMongoDataImportHandler`

---

## ShellMongoDataRunFileHandler
- 职责：MongoDB 脚本文件执行处理器，逐行解析并执行脚本。

- 字段：无（继承 `DBDataRunFileHandler`）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| ShellMongoDataRunFileHandler(dbClient, dbName) | 构造 | `super(dbClient, dbName)` |
| runFile() | 运行脚本文件 | 逐行读取，跳过单行/多行注释；`db.` 开头到 `;` 结束为一条命令则 `engine.eval`；异常经 `exception` 处理并可按配置继续；末尾 `doBatchInsert` 收尾 |

- 调用链：`MongoViewFactory.runScriptFile → new ShellMongoDataRunFileHandler → runFile → MongoScriptEngine.eval`

---

## ShellMongoDataTransportHandler
- 职责：MongoDB 数据传输处理器，在来源库与目标库之间传输集合数据与函数。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| sourceClient | MongoClient | 来源客户端 |
| targetClient | MongoClient | 目标客户端 |
| tables | List<ShellMongoDataTransportCollection> | 表 |
| functions | List<ShellMongoDataTransportFunction> | 函数 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| doTransport() | 执行传输 | 遍历 tables `transportTable`、functions `transportFunction` |
| transportTable(tableName) | 传输表 | 删/建目标集合，分页读源数据 `addInsertSql` |
| transportFunction(functionName) | 传输函数 | 删目标函数，读源函数后 `createFunction` |
| doBatchInsert(List<MongoRecord>) | 批量插入（覆盖） | 设置目标库名后 `targetClient.insertCollectionRecord` |
| setFunctions/getFunctions/setTables/getTables/setSourceClient/getSourceClient/setTargetClient/getTargetClient | 读写 | — |

- 调用链：`数据传输入口 → ShellMongoDataTransportHandler.doTransport → transportTable/transportFunction`

---

## DBDataDateTextFiled
- 职责：数据日期格式输入框，内置多种日期格式选项。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加日期格式选项 | `addItem("yyyy-MM-dd HH:mm:ss")` 等 12 种 |

- 调用链：`导出/导入 UI → DBDataDateTextFiled → config.setDateFormat`

---

## DBDataDumpTypeComboBox
- 职责：数据转储类型下拉框（数据和结构/结构）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加两项 | `I18nHelper.dataAndStructure/structure` |
| isFull() | 是否「数据和结构」 | `getSelectedIndex() == 0` |

- 调用链：`导出转储 UI → DBDataDumpTypeComboBox.isFull → handler.setDataType`

---

## DBDataFieldSeparatorComboBox
- 职责：数据字段分隔符下拉框。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加分号/逗号/空格项 | — |
| value() | 获取字段分隔符 | 按索引返回 ";"/","/" " |

- 调用链：`导出 UI → DBDataFieldSeparatorComboBox.value → config.setFieldSeparator`

---

## DBDataRecordLabelComboBox
- 职责：数据记录标签下拉框。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加 "(Root)"/"RECORDS" 项 | — |
| isRoot() | 是否根标签 | `getSelectedIndex() == 0` |

- 调用链：`导入 UI → DBDataRecordLabelComboBox.isRoot`

---

## DBDataRecordSeparatorComboBox
- 职责：数据记录分隔符下拉框（CRLF/LF/CR）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加项并按系统选中 | Windows 选 0，Linux/macOS 选 1 |
| value() | 获取记录分隔符 | 返回 "\r\n"/"\n"/"\r" |

- 调用链：`导出 UI → DBDataRecordSeparatorComboBox.value → config.setRecordSeparator`

---

## DBDataTxtIdentifierComboBox
- 职责：数据文本标识符下拉框（双引号/单引号）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
|（实例初始化块）| 添加 `"`/`'` 项 | — |

- 调用链：`导出 UI → DBDataTxtIdentifierComboBox`

---

## ShellMongoDataExportCollectionComboBox
- 职责：数据导出集合下拉框。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| initNode() | 初始化节点（覆盖） | 设置 `SimpleStringConverter` 展示集合名后 `super.initNode()` |

- 调用链：`导出 UI → ShellMongoDataExportCollectionComboBox`

---

## ShellMongoDataExportCollectionTableView
- 职责：数据导出集合表格视图。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getSelectedTables() | 已选集合列表 | 过滤 `isSelected` |
| hasSelectedTable() | 是否存在已选集合 | 遍历判断 |

- 调用链：`导出 UI → getSelectedTables → DBDataExportHandler.tables`

---

## ShellMongoDataExportColumnListView
- 职责：数据导出列列表视图（复选框）。

- 字段：无

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| init(List<ShellMongoDataExportColumn>) | 初始化列表 | 为每列建 `FXCheckBox` 并双向同步选中 |

- 调用链：`导出 UI → init(columns) → column.setSelected`

---

## ShellMongoDataImportFileTableView
- 职责：数据导入文件表格视图。

- 字段：无

- 方法：无（继承 `FXTableView<ShellMongoDataImportFile>`）

- 调用链：`导入 UI → ShellMongoDataImportFileTableView`

---

## ShellMongoDataTransportFunctionListView
- 职责：数据传输函数列表视图（复选框）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| selectedChanged | Runnable | 选中项变更回调 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| getSelectedChanged/setSelectedChanged | 读写回调 | — |
| of(List<MongoFunction>) | 由 Mongo 函数构建 | 转为 `ShellMongoDataTransportFunction` 后 `init` |
| init(List<ShellMongoDataTransportFunction>) | 初始化列表 | 建复选框绑定选中并触发回调 |
| getSelectedFunctions() | 已选函数列表 | 遍历复选框取 prop("data") |
| getSelectedSize() | 已选数量 | 计数 |

- 调用链：`数据传输 UI → of(functions) → init → getSelectedFunctions → handler.setFunctions`

---

## ShellMongoDataTransportTableListView
- 职责：数据传输集合列表视图（复选框）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| selectedChanged | Runnable | 选中项变更回调 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| of(List<MongoCollection>) | 由 Mongo 集合构建 | 转为 `ShellMongoDataTransportCollection` 后 `init` |
| init(List<ShellMongoDataTransportCollection>) | 初始化列表 | 建复选框绑定选中并触发回调 |
| getSelectedTables() | 已选集合列表 | 遍历取 prop("data") |
| getSelectedSize() | 已选数量 | 计数 |
| getSelectedChanged/setSelectedChanged | 读写回调 | — |

- 调用链：`数据传输 UI → of(tables) → init → getSelectedTables → handler.setTables`
