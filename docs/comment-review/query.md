# query 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/query/`
>
> 说明：本包实现查询编辑器的词元分析、自动补全提示与执行结果承载。

## MongoExecuteResult

- 职责：脚本执行结果，继承 `MongoQueryResult`，用于非查询类（更新/删除）执行的返回。

- 字段：（无自有字段）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `hasResult()` | 是否存在结果 | `updateCount > 0` 返回 false，否则委托 `super.hasResult()` |

- 调用链：`脚本执行 → MongoExecuteResult → hasResult（据变更数判断）`

## MongoQueryEditor

- 职责：查询编辑器，继承 `SqlEditor`，集成补全提示框与快捷键注释/取消注释。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `promptPopup` | `MongoQueryPromptPopup`（final） | 提示词弹窗 |
| `runCallback` | `Runnable` | 运行回调，由外部注入 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造块 | 绑定交互 | 鼠标释放/失焦时隐藏弹窗；设置弹窗项选中回调为 `promptPopup.autoComplete(this, item)`；按键释放时 `Ctrl+/` 触发 `doComment()`，否则调 `promptPopup.prompt(this, event)` |
| `doComment()` | 注释/取消注释选中行 | 取选区行：若全部以 `-- ` 开头则取消注释（移除前 3 字符），否则加 `// ` 前缀；无内容时插入 `// `；按边距 `NumberUtil.checkBound` 逐行处理并修正选区 |
| `getMenuItems()` | 编辑器右键菜单 | 有选中文本时前置「运行选中」项与分隔符，其余拼接 `super.getMenuItems()` |
| `setRunCallback(Runnable)` | 设置运行回调 | 保存回调 |
| `run()` | 运行 | 回调非空则执行 |

- 调用链：
  - `按键释放 → MongoQueryEditor 构造块 → MongoQueryPromptPopup.prompt → MongoQueryTokenAnalyzer.currentToken`
  - `Ctrl+/ → doComment → 文本改写 + selectRange`

## MongoQueryPromptItem

- 职责：查询提示词数据项（POJO）。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `type` | `byte` | 类型：1 集合、2 函数、4 关键字 |
| `content` | `String` | 内容 |
| `correlation` | `double` | 相关度 |
| `extContent` | `String` | 额外内容（集合类型显示） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `isFunctionType()` / `isCollectionType()` / `isKeywordType()` | 类型判断 | 分别判断 `type` 是否为 2/1/4 |
| `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`MongoQueryTokenAnalyzer.initPrompts → new MongoQueryPromptItem → MongoQueryPromptListView.init`

## MongoQueryPromptListView

- 职责：查询提示列表视图，继承 `FXListView<FXHBox>`，渲染提示词并支持上下选择与背景高亮。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `currentPickIndex` | `int`（volatile） | 当前选中项索引，初始 -1 |
| `onItemPicked` | `Runnable` | 项被双击确认的回调 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| 构造块 | 初始化尺寸 | 宽 360、高 240、无内边距 |
| `select(int index)` | 选中指定项 | 索引越界钳制后 `super.select` 并 `applyBackground(index)` |
| `pickNext()` / `pickPrev()` | 下移/上移选择 | 基于 `currentPickIndex` 前后移动，加同步锁 |
| `hasPicked()` | 是否存在有效选中 | 有选中项且 `currentPickIndex != -1` |
| `getPickedItem()` | 获取选中提示词 | 从选中 `FXHBox` 取属性 `item`，并清除背景 |
| `applyBackground(int)` | 应用选中背景 | 清除旧项背景，给新项设置 `Color.DEEPSKYBLUE` 背景 |
| `init(List<MongoQueryPromptItem>)` | 初始化列表 | 清除背景，逐项构建 `FXHBox`（图标标签 + 额外标签），属性 `item` 存数据 |
| `initPromptLabel(item)` | 构建提示标签 | 按类型选图标：关键字 `KeywordsSVGGlyph`、集合 `TableSVGGlyph`、函数 `FunctionSVGGlyph` |
| `initExtLabel(item)` | 构建额外标签 | 仅集合类型显示灰色 `extContent` |
| `initBox(FXHBox)` | 初始化行容器 | 高 20、手型光标；单击高亮，双击触发 `onItemPicked` |
| `getOnItemPicked()` / `setOnItemPicked(Runnable)` | 获取/设置回调 | 无额外逻辑 |

- 调用链：`MongoQueryPromptPopup.initPrompts → listView().init(items) → initBox/applyBackground`

## MongoQueryPromptPopup

- 职责：查询提示框，继承 `FXPopup`，负责按键驱动的补全提示展示、上下选择、回车确认与文本替换。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `onItemSelected` | `Consumer<MongoQueryPromptItem>` | 提示项选中事件 |
| `token` | `MongoQueryToken` | 当前词元 |
| `promptFlag` | `AtomicInteger`（final） | 提示标志位，用于延迟任务去重 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoQueryPromptPopup()` | 构造 | 自动修正/自动隐藏，`initContent()`、`changeTheme(ThemeManager.currentTheme())` |
| `initContent()` | 初始化内容 | 创建 `MongoQueryPromptListView`（字号 12、手型），设置 `onItemPicked` 为 `pickItem()` 后隐藏 |
| `listView()` | 获取列表组件 | 取内容首家元素 |
| `initPrompts(MongoQueryToken)` | 初始化提示词 | `MongoQueryTokenAnalyzer.INSTANCE.initPrompts(token, 0.5f)`，填充列表，返回是否非空 |
| `prompt(MongoQueryEditor, KeyEvent)` | 执行提示主逻辑 | 常规快捷键（Ctrl+S/X/V/C/A/Z/Y/`/`）直接隐藏；已显示时处理 DOWN 下移、UP 上移、ENTER 确认；`UPDATE_CODES`（退格/删除）隐藏；非 `PROMPT_CODES` 或含主修饰键隐藏；否则取光标处词元，用 `promptFlag` 生成标志并在 30ms 延迟任务中生成提示，命中则 `show(area)` |
| `show(MongoQueryEditor)` | 显示提示框 | `RenderService.submitFXLater` 中取光标位置 `getCaretBounds()`，在其右下方偏移显示 |
| `hide()` | 隐藏 | 显示中则 `FXUtil.runWait(super::hide)`，并清空 `token` |
| `autoComplete(editor, item)` | 自动完成替换 | 用词元起止位置 `replaceText` 替换为提示内容，并修正光标位置 |
| `pickItem()` | 确认选中项 | 显示中使用 `listView().getPickedItem()` 触发 `onItemSelected` |
| `isGeneralKeyEvent(KeyEvent)` | 是否常规快捷键 | 判断 Ctrl 组合的 S/X/V/C/A/Z/Y/`/` |
| `getOnItemSelected()` / `setOnItemSelected(...)` | 获取/设置选中事件 | 无额外逻辑 |

- 调用链：
  - `MongoQueryEditor 按键 → MongoQueryPromptPopup.prompt → MongoQueryTokenAnalyzer.currentToken → initPrompts → show`
  - `回车/双击 → pickItem → onItemSelected → MongoQueryEditor 构造块的 autoComplete → replaceText`

## MongoQueryResult

- 职责：查询结果抽象基类，承载脚本、耗时、消息、字段与记录数据，并提供列/集合/主键等派生信息。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `script` | `String` | 脚本内容 |
| `used` | `long` | 耗时（微秒） |
| `msg` | `String` | 消息 |
| `updateCount` | `long` | 变更总数 |
| `success` | `boolean` | 是否成功 |
| `columns` | `MongoColumns` | 字段列表 |
| `records` | `List<MongoRecord>` | 行列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `hasResult()` | 是否存在查询结果 | 记录非空返回 true；否则看 `columns` 是否为空 |
| `parseResult(List<MongoRecord>)` | 解析结果 | 保存记录并由 `MongoRecordUtil.columns(records)` 推导列 |
| `dbName()` | 数据库名称 | 取首列 `getDbName()` |
| `collectionName()` | 集合名称 | 取首列 `getCollectionName()` |
| `getPrimaryKey()` | 主键列 | 返回 `is_id()` 的列 |
| `isUpdatable()` | 是否可更新 | 存在 `is_id()` 列即为 true |
| `getCount()` | 结果行数 | `records.size()`，为空返回 0 |
| `getUsedMs()` | 耗时（毫秒） | `used / 1_000_000` |
| `columnList()` | 列列表 | `columns` 为空返回空列表 |
| `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`查询执行 → MongoQueryResult.parseResult → MongoRecordUtil.columns`

## MongoQueryResults

- 职责：查询结果集容器，承载多条结果或错误信息。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `errMsg` | `String` | 错误消息 |
| `results` | `List<R>` | 结果列表（泛型 `R extends MongoQueryResult`） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `addResult(R)` | 添加结果 | 懒初始化列表后加入 |
| `isEmpty()` | 是否为空 | `CollectionUtil.isEmpty(results)` |
| `isSuccess()` | 是否成功 | `errMsg` 为空即成功 |
| `parseError(Exception)` | 解析异常 | `errMsg = ex.getMessage()` |
| `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`查询批量执行 → MongoQueryResults.addResult → 结果渲染`

## MongoQueryToken

- 职责：查询词元（POJO），记录词元字符、内容与在文本中的起止位置，并据词元字符推断可能的补全类型。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `endIndex` | `int` | 结束位置 |
| `startIndex` | `int` | 开始位置 |
| `content` | `String` | 内容 |
| `token` | `Character` | 词元字符标识（空格/`.`/`"` 等） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `isEmpty()` / `isNotEmpty()` | 内容判空 | 委托 `StringUtil` |
| `isPossibilityKeyword()` | 是否可能为关键字 | 词元字符为空格或换行 |
| `isPossibilityFunction()` | 是否可能为函数 | 词元字符为 `.` |
| `isPossibilityCollection()` | 是否可能为集合 | 词元字符为 `"` |
| `getXxx/setXxx` | 标准 getter/setter | 无额外逻辑 |

- 调用链：`MongoQueryTokenAnalyzer.currentToken → MongoQueryToken → isPossibility*（决定补全类型）`

## MongoQueryTokenAnalyzer

- 职责：查询词元分析器（单例），从光标处向前扫描定位当前词元，并按类型生成带相关度排序的提示词列表。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `INSTANCE` | `MongoQueryTokenAnalyzer`（static final） | 单例 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `currentToken(content, currentIndex)` | 获取光标处词元 | 截取到光标的内容并反转，反向扫描首个 `\n`/空格/`.`/`"` 作为词元边界（未找到则为 `\0` 关键字）；据此计算起止位置并构造 `MongoQueryToken`（内容 `trim`） |
| `initPrompts(token, minCorr)` | 生成提示词列表 | 按 `isPossibilityKeyword/Collection/Function` 分别对 `MongoQueryUtil.getKeywords/getCollections/getFunctions` 并行计算 `TextUtil.clacCorr`，相关度 `> minCorr` 的包装为 `MongoQueryPromptItem`（type 4/1/2）；用 `ThreadUtil.submit` 提交任务；收集后按相关度升序排序并反转（降序）返回 |

- 调用链：
  - `MongoQueryPromptPopup.prompt → currentToken`
  - `MongoQueryPromptPopup.initPrompts → MongoQueryTokenAnalyzer.initPrompts → MongoQueryUtil + TextUtil.clacCorr`

## MongoQueryUtil

- 职责：查询工具类，维护补全所需的关键字、函数与集合索引，并提供异步索引刷新。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `indexStatus` | `int`（static） | 索引状态：0 未初始化、1 初始化中、2 已初始化 |
| `DB_KEYWORDS` | `Set<String>`（static final） | 关键字集合，静态块加入 `db` |
| `DB_FUNCTIONS` | `Set<String>`（static final） | 函数集合，静态块加入 `MongoScriptUtil.databaseFuncions()` 与 `collectionFuncions()` |
| `DB_COLLECTIONS` | `List<MongoCollection>`（static final） | 集合列表 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getKeywords()` / `getFunctions()` / `getCollections()` | 获取关键字/函数/集合 | 返回对应集合 |
| `updateIndex(client, dbName)` | 异步更新集合索引 | `ThreadUtil.start` 异步执行：仅当 `indexStatus == 0` 时置 1、清空并用 `client.listCollections(dbName)` 重建列表，成功后置 2，异常回置 0 |

- 调用链：`MongoQueryTokenAnalyzer.initPrompts → MongoQueryUtil.getKeywords/getFunctions/getCollections`

## ShellQueryUtil

- 职责：查询工具类，定义触发补全提示与触发提示框更新的按键常量集合。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `PROMPT_CODES` | `List<KeyCode>`（static final） | 触发提示的按键：`-`、空格、`_`、26 个字母、小键盘/软盘/主键盘数字 |
| `UPDATE_CODES` | `List<KeyCode>`（static final） | 触发更新的按键：`BACK_SPACE`、`DELETE` |

- 方法：无（仅静态块初始化两个按键集合）

- 调用链：`MongoQueryPromptPopup.prompt → ShellQueryUtil.PROMPT_CODES / UPDATE_CODES 判定`
