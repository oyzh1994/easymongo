# root（根目录）代码审查

> 源码路径：`src/main/java/cn/oyzh/easymongo/`

## EasyMongoApp

- 职责：程序主入口，继承 `FXApplication`，负责运行环境初始化、存储初始化、事件总线注册、主界面启动与退出清理。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `PROJECT` | `Project`（static final） | 项目信息，由 `Project.load()` 载入（含项目名等） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `main(String[])` | 程序主方法，初始化运行环境并启动应用 | 设置默认未捕获异常处理器（过滤 `isImageAutoSize`）→ `SysConst.projectName/storeDir/cacheDir`（路径来自 `MongoConst`）→ `MongoStoreUtil.init()` 存储初始化 → 设置 `FXConst.appIcon` → `EventFactory.registerEventBus(FXEventBus.class)` 并注册 SYNC/ASYNC/DEFAULT 三种事件配置 → `launch(EasyMongoApp.class, args)` |
| `init()` | FX 应用初始化 | 记录 `FXConst.INSTANCE = this`；禁用 CSS 日志；读取 `MongoSettingStore.SETTING`，依次 `I18nManager.apply(locale)`、`FontManager.apply(fontConfig)`、`ThemeManager.apply(themeConfig)`、`OpacityManager.apply(opacityConfig)`；注册异常解析器 `MessageBox.registerExceptionParser(MongoExceptionParser.INSTANCE)`；`EventListener.super.register()`；`super.init()` |
| `start(Stage)` | FX 应用启动 | `super.start()`；`TerminalManager.setLoadHandler(MongoTerminalPane.TERMINAL_NAME, MongoTerminalManager::registerHandlers)` 注册终端命令处理器；`SystemUtil.gcInterval(5_000)` 开启定期 GC |
| `stop()` | FX 应用退出 | `EventListener.super.unregister()` 取消事件注册 → `super.stop()` |
| `showMainView()` | 显示主界面 | `StageManager.showStage(MainController.class)` |
| `onEventMsg(EventFormatter)` | 事件消息订阅（`@EventSubscribe`） | 将事件格式化器加入 `ShellMessageTabController.EVENT_MESSAGES` |

- 调用链：
  - `EasyMongoBootstrap.main → EasyMongoApp.main → EasyMongoApp.init → showMainView → StageManager.showStage(MainController)`
  - `EasyMongoApp.start → MongoTerminalManager.registerHandlers`
  - `事件总线 → EasyMongoApp.onEventMsg → ShellMessageTabController.EVENT_MESSAGES`

## EasyMongoBootstrap

- 职责：程序启动器，仅作为可执行入口，将启动委托给 `EasyMongoApp`。

- 字段：（无）

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `main(String[])` | 程序主方法 | 直接调用 `EasyMongoApp.main(args)` |

- 调用链：`EasyMongoBootstrap.main → EasyMongoApp.main`

## MongoConst

- 职责：MongoDB 常量类，集中定义图标路径并提供存储/缓存目录计算。

- 字段：

| 字段 | 类型 | 含义 |
|---|---|---|
| `ICON_PATH` | `String`（static final） | 应用图标地址（`/image/db.png`） |
| `TRAY_ICON_PATH` | `String`（static final） | 托盘图标地址（`/image/db.png`） |

- 方法：

| 方法 | 说明 | 关键逻辑/调用 |
|---|---|---|
| `getStorePath()` | 获取存储路径 | 在 Jar 内运行时返回 `~/.easymongo/`，否则返回 `~/.easymongo_dev/`（开发态隔离） |
| `getCachePath()` | 获取缓存路径 | `getStorePath() + "cache/"` |

- 调用链：
  - `EasyMongoApp.main → MongoConst.getStorePath / getCachePath → SysConst.storeDir / cacheDir`
