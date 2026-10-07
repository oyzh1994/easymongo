# fx 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/fx/`
>
> 说明：本包为自定义 JavaFX 控件与表格组件，多数继承 `cn.oyzh.fx.plus` 下的基类。

## CodeTextFiled

- 职责：`org.bson.types.Code` 类型的文本编辑框，继承 `JsonTextFiled`，提供 Code 值的读取与格式化。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `skin()` | 返回皮肤 | 强制转换为 `CodeTextFiledSkin` |
| `createDefaultSkin()` | 创建默认皮肤 | `new CodeTextFiledSkin(this)` |
| `setArray(boolean)` | 禁止数组模式 | 直接抛 `UnsupportedOperationException` |
| `getValue()` | 获取值 | 将当前文本包装为 `new Code(text)` |
| `formatValue()` | 格式化值 | `setText(format(super.value()))` |
| `format(Object)`（static） | 格式化 Code 值 | `CharSequence` 取 `toString`；`Code` 取 `getCode()`；其余 `toString` |

- 调用链：`Code 字段编辑 → CodeTextFiled.getValue() → new Code(text)`

## CodeTextFiledSkin

- 职责：`CodeTextFiled` 的皮肤，继承 `LongTextFiledSkin`，使用 SQL 语法高亮格式。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `CodeTextFiledSkin(TextField)` | 构造皮肤 | `super(textField)` |
| `getFormatType()` | 获取编辑器格式类型 | 返回 `EditorFormatType.SQL` |

- 调用链：`CodeTextFiled.createDefaultSkin → CodeTextFiledSkin → getFormatType`

## DBStatusColumn

- 职责：数据库对象状态列，泛型 `S extends DBObjectStatus`，仅显示状态图标。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `DBStatusColumn()` | 构造状态列 | `PropertyValueFactory("status")`；固定宽 25、不可排序/缩放/重排；标题为 `I18nHelper.status()` |
| `initNode()` | 初始化节点 | `showGraphicOnlyLater()` 仅显示图标后 `super.initNode()` |

- 调用链：`记录/对象表格 → DBStatusColumn（status 属性绑定）`

## MongoColumnComboBox

- 职责：MongoDB 字段选择框，继承 `FXComboBox<MongoColumn>`。

- 字段：（无，构造块设置 `SimpleStringConverter` 以 `displayName()` 显示）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoColumnComboBox()` / `MongoColumnComboBox(List<MongoColumn>)` | 构造字段选择框 | 后者 `addItems(columns)` |
| `select(String colName)` | 按字段名选中 | 忽略大小写遍历匹配 `getName()`，命中后 `select(object)` |
| `getColumnName()` | 获取选中字段名 | `getSelectedItem().getName()` |

- 调用链：`数据过滤弹窗 → MongoColumnComboBox.select(colName)`

## MongoConditionComboBox

- 职责：MongoDB 查询条件选择框，继承 `FXComboBox<MongoCondition>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `initNode()` | 初始化节点 | 设置以 `MongoCondition.getName()` 显示的转换器；`addItem(MongoConditionUtil.conditions())` 载入全部条件 |

- 调用链：`MongoConditionComboBox.initNode → MongoConditionUtil.conditions()`

## MongoConnectComboBox

- 职责：MongoDB 连接选择框，继承 `FXComboBox<MongoConnect>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `initNode()` | 初始化节点 | 设置以 `MongoConnect.getName()` 显示的转换器；`MongoConnectStore.INSTANCE.selectList()` 载入连接列表后 `setItem` |

- 调用链：`MongoConnectComboBox.initNode → MongoConnectStore.INSTANCE.selectList`

## MongoJoinSymbolComboBox

- 职责：MongoDB 条件连接符号选择框（AND/OR），继承 `FXComboBox<String>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `initNode()` | 初始化节点 | 依次 `addItem("AND")`、`addItem("OR")` |

- 调用链：`查询条件编辑 → MongoJoinSymbolComboBox（AND/OR 拼接）`

## MongoRecordColumn

- 职责：MongoDB 记录字段列，继承 `FXTableColumn<MongoRecord,Object>` 并实现 `MenuItemAdapter`/`ContextMenuAdapter`，支持字段名/类型的图文表头与右键复制字段名。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `column` | `MongoColumn`（final） | 对应字段 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `MongoRecordColumn(MongoColumn)` | 构造列（图文模式） | 委托 `this(column, 1)` |
| `MongoRecordColumn(MongoColumn, int mode)` | 构造列 | 设置可重排；`cellValueFactory` 取 `record.getProperty(column.getName())`；`mode==0` 仅文本，否则 `initContent()` 生成图文表头并 `text(displayName())`、`showGraphicOnlyLater()` |
| `initContent()` | 构建图文表头 | 纵向 `FXVBox`：加粗字段名 `FXLabel` + 绿色字段类型 `FXLabel`；`setOnContextMenuRequested` 弹出右键菜单；监听表头高度实时同步 `MongoRecordTableView.setHeaderHeight` |
| `getMenuItems()` | 菜单项 | `MenuItemHelper.copyColumnName(this::copyColumnName)` |
| `copyColumnName()` | 复制字段名到剪贴板 | `ClipboardUtil.copy(getName())` |
| `getFont()` | 获取当前字体 | `FontManager.currentFont()` |
| `getName()` / `getType()` | 字段名/类型 | 取自 `column` |

- 调用链：
  - `MongoRecordTableView 初始化列 → MongoRecordColumn(column, mode) → initContent → 表头高度监听`
  - `右键表头 → getMenuItems → copyColumnName → ClipboardUtil.copy`

## MongoRecordTableRow

- 职责：MongoDB 记录表格行，继承 `FXTableRow<MongoRecord>`（空实现，仅作类型标识）。

- 字段：（无）

- 方法：（无）

- 调用链：`MongoRecordTableView.initNode → setRowFactory → new MongoRecordTableRow`

## MongoRecordTableView

- 职责：MongoDB 记录表格视图，继承 `FXTableView<MongoRecord>`，支持多选并处理行销毁。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `hasProperty(MongoRecordProperty)` | 是否存在含该属性的记录 | 遍历 `getItems()`，任一 `record.hasProperty` 命中返回 true |
| `hasRecord(MongoRecord)` | 是否包含指定记录 | `getItems().contains(record)` |
| `initNode()` | 初始化节点 | 多选模式；`setRowFactory(new MongoRecordTableRow())`；`destroyItemsOnRemoved()` 移除时销毁；`super.initNode()` |

- 调用链：`MongoRecordTableView.initNode → MongoRecordTableRow`；`hasProperty → MongoRecord.hasProperty`

## MonogoAuthMethodComboBox

- 职责：MongoDB 认证方式选择框（none/password），继承 `FXComboBox<String>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getType()` | 获取当前认证方式 | 索引 0 → `none`，1 → `password`，其余 `unknown` |
| `select(String)` | 按认证方式选中 | `none`→索引 0，`password`→索引 1 |
| `initNode()` | 初始化节点 | 添加 `I18nHelper.none()`、`I18nHelper.password()` 两项后 `super.initNode()` |

- 调用链：`连接配置 → MonogoAuthMethodComboBox → MongoConnect.authType`

## ShellDataEditor

- 职责：命令行数据编辑器，继承 `Editor`，按设置返回编辑器字体。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getEditorFont()` | 获取编辑器字体 | 读取 `MongoSettingStore.SETTING.editorFontConfig()` 并 `FontManager.toFont` |

- 调用链：`ShellDataEditor.getEditorFont → MongoSettingStore.SETTING（editorFontConfig）`

## ShellMongoCollectionComboBox

- 职责：MongoDB 集合选择框，继承 `FXComboBox<String>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `init(dbName, client)` | 初始化集合列表 | 委托 `init(dbName, null, client)` |
| `init(dbName, tableName, client)` | 初始化并选中集合 | `client.listCollections(dbName)` 取集合名（并行流 `map(getName)`）设置项；`tableName` 非空则 `select`，否则 `clearChild()` |

- 调用链：`ShellMongoCollectionComboBox.init → MongoClient.listCollections`

## ShellMongoDatabaseComboBox

- 职责：MongoDB 数据库选择框，继承 `FXComboBox<String>`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `init(client)` | 初始化数据库列表 | 委托 `init(client, null)` |
| `init(client, dbName)` | 初始化并选中数据库 | `clearItems()`；`client.listDatabaseNames()` 非空则 `setItem`；`dbName` 非空则 `select` |

- 调用链：`ShellMongoDatabaseComboBox.init → MongoClient.listDatabaseNames`
