# controller 包代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/controller/`
>
> 说明：本包为 JavaFX FXML 控制器，继承 `cn.oyzh.fx.plus` 下的控制器基类。

## AboutController

- 职责：关于窗口的控制器，展示项目名称、版本、更新日期、版权与构建类型。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `name` | `FXText`（`@FXML`） | 项目名称 |
| `type` | `FXText`（`@FXML`） | 构建类型 |
| `version` | `FXText`（`@FXML`） | 项目版本 |
| `updateDate` | `FXText`（`@FXML`） | 更新日期 |
| `copyright` | `FXText`（`@FXML`） | 版权信息 |
| `project` | `Project`（final） | 项目信息，`Project.load()` |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `onWindowShown(WindowEvent)` | 窗口显示时填充信息 | 设置名称、`v+版本`、更新日期、版权；按 `type == "build"` 选择 `buildType1/buildType2`；追加标题并 `hideOnEscape()` |
| `getViewTitle()` | 视图标题 | `I18nResourceBundle.i18nString("base.title.about")` |

- 调用链：`HeaderController.about → StageManager.showStage(AboutController) → onWindowShown`

## HeaderController

- 职责：主页头部控制器，提供布局切换、数据传输、设置、关于、退出、消息等入口。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `layoutPane` | `LayoutSVGPane`（`@FXML`） | 布局切换组件 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `layout()` | 切换左侧栏布局 | 当前为布局 1 则 `MongoEventUtil.layout2()`，否则 `layout1()` |
| `transport()` | 打开数据传输窗口 | 已存在 `ShellMongoDataTransportController` 舞台则 `toFront()`，否则 `showStage` |
| `setting()` | 打开设置窗口 | 已存在 `SettingController` 则 `toFront()`，否则 `showStage(SettingController.class, this.stage)` |
| `about()` | 打开关于窗口 | `StageManager.showStage(AboutController.class, this.stage)` |
| `quit()` | 退出应用 | 确认后 `StageManager.exit()` |
| `message()` | 显示消息 | `MongoEventUtil.showMessage()` |
| `tool()` | 工具箱（空实现） | 无 |
| `layout1(Layout1Event)` | 布局 1 事件（`@EventSubscribe`） | 设置提示文字 `showLeftSide()` 并 `layoutPane.layout1()` |
| `layout2(Layout2Event)` | 布局 2 事件 | 设置提示文字 `hiddenLeftSide()` 并 `layoutPane.layout2()` |
| `onWindowShowing(WindowEvent)` | 窗口显示中 | 设置提示文字为「隐藏左侧」 |

- 调用链：
  - `点击设置 → HeaderController.setting → StageManager.showStage(SettingController)`
  - `事件 Layout1Event/Layout2Event → HeaderController.layout1/layout2 → LayoutSVGPane`

## MainController

- 职责：主窗口控制器，继承 `ParentStageController`，负责子控制器聚合、退出策略、窗口尺寸/位置记忆。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `project` | `Project`（final） | 项目信息 |
| `headerController` | `HeaderController`（`@FXML`） | 头部页面子控制器 |
| `mysqlMainController` | `MongoMainController`（`@FXML`） | db 主页子控制器 |
| `setting` | `MongoSetting`（final） | 当前设置，来自 `MongoSettingStore.SETTING` |
| `settingStore` | `MongoSettingStore`（final） | 设置存储 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getSubControllers()` | 返回子控制器 | `Arrays.asList(mysqlMainController, headerController)` |
| `onWindowCloseRequest(WindowEvent)` | 关闭请求处理 | 按 `setting` 退出策略：`isExitDirectly` 直接 `StageManager.exit()`；`isExitAsk` 确认后退出或 `event.consume()`；`isExitTray` 存在托盘则 `TrayManager.show()` |
| `onSystemExit()` | 系统退出处理 | 按 `isRememberPageSize` 保存宽高与最大化状态；按 `isRememberPageLocation` 保存屏幕坐标；`settingStore.replace(setting)`；`TrayManager.destroy()` |
| `onStageInitialize(StageAdapter)` | 舞台初始化 | 按设置恢复最大化/尺寸/位置 |
| `onWindowShown(WindowEvent)` | 窗口显示（仅 super 与注释残留） | `super.onWindowShown(event)` |
| `getViewTitle()` | 视图标题 | `I18nResourceBundle.i18nString("mongo.title.main")` |

- 调用链：
  - `窗口关闭 → MainController.onWindowCloseRequest → StageManager.exit / TrayManager.show`
  - `应用退出 → onSystemExit → MongoSettingStore.replace → TrayManager.destroy`

## MongoMainController

- 职责：db 主页控制器，管理左侧连接面板与右侧标签面板的分栏布局，并刷新标题。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `root` | `FXSplitPane`（`@FXML`） | 根分栏容器 |
| `connect` | `FXVBox`（`@FXML`） | 左侧连接面板 |
| `tabPane` | `MongoTabPane`（`@FXML` public） | db 标签面板 |
| `connectController` | `ConnectController`（`@FXML`） | 连接面板子控制器 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `flushViewTitle(MongoConnect)` | 刷新窗口标题 | 连接非空 `stage.appendTitle(" (name)")`，否则 `stage.restoreTitle()` |
| `treeItemChanged(MongoTreeItemChangedEvent)` | 树节点变化事件（`@EventSubscribe`） | 若节点为 `MongoConnectTreeItem` 则 `flushViewTitle(treeItem.value())`，否则清空标题 |
| `layout2(Layout2Event)` | 布局 2 事件 | 显示左侧连接面板，恢复分隔条与位置 |
| `layout1(Layout1Event)` | 布局 1 事件 | 隐藏左侧连接面板，记录并隐藏分隔条 |
| `getSubControllers()` | 返回子控制器 | `List.of(connectController)` |

- 调用链：
  - `事件 MongoTreeItemChangedEvent → MongoMainController.treeItemChanged → flushViewTitle`
  - `事件 Layout1/Layout2Event → MongoMainController.layout1/layout2 → FXSplitPane`

## SettingController

- 职责：应用设置窗口控制器，集中管理退出方式、页面记忆、字体、主题、区域、透明度等配置的读取、编辑、重置、保存与应用。

- 字段（`@FXML` 控件）：

| 字段 | 类型 | 含义 |
|---|---|---|
| `root` | `SettingMainPane` | 设置主面板 |
| `exitMode` | `FXToggleGroup` | 退出方式分组 |
| `exitMode0` / `exitMode1` / `exitMode2` | `RadioButton` | 三种退出方式 |
| `pageSize` | `FXCheckBox` | 记住页面大小 |
| `pageResize` | `FXCheckBox` | 记住页面拉伸 |
| `pageLocation` | `FXCheckBox` | 记住页面位置 |
| `keyLoadLimit` | `NumberTextField` | 键加载限制 |
| `theme` | `ThemeComboBox` | 主题 |
| `bgColor` / `fgColor` / `accentColor` | `FXColorPicker` | 背景/前景/强调色 |
| `bgColorBox` / `fgColorBox` / `accentColorBox` | `FXHBox` | 颜色重置面板 |
| `fontSize` / `fontWeight` / `fontFamily` | `FontSizeComboBox` / `FontWeightComboBox` / `FontFamilyComboBox` | 通用字体配置 |
| `editorFontSize` / `editorFontWeight` / `editorFontFamily` | 同上 | 编辑器字体配置 |
| `terminalFontSize` / `terminalFontWeight` / `terminalFontFamily` | 同上 | 终端字体配置 |
| `locale` | `LocaleComboBox` | 区域 |
| `opacity` / `titleBarOpacity` | `FXSlider` | 窗口/标题栏透明度 |
| `setting` | `MongoSetting`（final） | 当前设置对象 |
| `settingStore` | `MongoSettingStore`（final） | 设置存储 |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `onWindowShowing(WindowEvent)` | 窗口显示中回填配置 | 按 `exitMode` 勾选单选；回填页面记忆复选、主题与三色（空时取主题默认）、字体/编辑器字体/终端字体、区域、透明度 |
| `saveSetting()` | 保存设置 | 读取各控件值写入 `setting`（字体、主题、颜色、区域、透明度、页面记忆、退出方式）；`settingStore.update(setting)` 成功则关闭窗口并依次 `I18nManager/FontManager/ThemeManager/OpacityManager.apply`；若 `checkConfigForRestart` 有提示且用户确认则 `MongoProcessUtil.restartApplication()` |
| `checkConfigForRestart(String locale)` | 检查是否需重启 | 区域与当前设置不一致时返回 `base.restartTip1` 提示文案，否则空串 |
| `bindListeners()` | 绑定监听 | 前景/背景色面板禁用状态绑定强调色面板；主题切换时同步三色并控制系统主题下禁用强调色 |
| `onWindowShown(WindowEvent)` | 窗口显示时构建左树 | 向 `SettingLeftTreeView` 添加 Mongo/窗口/字体（含通用、编辑器、终端）/主题/区域节点并选中；`hideOnEscape()` |
| `resetFgColor()` / `resetBgColor()` / `resetAccentColor()` | 重置颜色 | 由 `theme` 取值重置对应颜色 |
| `resetLocale()` | 重置区域 | `locale.select((String) null)` |
| `resetOpacity()` / `resetTitleBarOpacity()` | 重置透明度 | 设为 `OpacityManager.defaultOpacity * 100` |
| `resetFontFamily/Size/Weight()` | 重置通用字体 | 取 `AppSetting.defaultFont*` |
| `resetEditorFontFamily/Size/Weight()` | 重置编辑器字体 | 取 `AppSetting.defaultEditorFont*` |
| `resetTerminalFontFamily/Size/Weight()` | 重置终端字体 | 取 `AppSetting.defaultTerminalFont*` |
| `getViewTitle()` | 视图标题 | `I18nHelper.settingTitle()` |

- 调用链：
  - `HeaderController.setting → StageManager.showStage(SettingController) → onWindowShowing/onWindowShown`
  - `saveSetting → MongoSettingStore.update → I18nManager/FontManager/ThemeManager/OpacityManager.apply`
  - `区域变更 → checkConfigForRestart → MongoProcessUtil.restartApplication`
