package cn.oyzh.easymongo;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.exception.ExceptionUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.system.SystemUtil;
import cn.oyzh.easymongo.controller.MainController;
import cn.oyzh.easymongo.controller.SettingController;
import cn.oyzh.easymongo.domain.MongoSetting;
import cn.oyzh.easymongo.exception.MongoExceptionParser;
import cn.oyzh.easymongo.store.MongoSettingStore;
import cn.oyzh.easymongo.store.MongoStoreUtil;
import cn.oyzh.easymongo.terminal.MongoTerminalManager;
import cn.oyzh.easymongo.terminal.MongoTerminalPane;
import cn.oyzh.event.EventFactory;
import cn.oyzh.fx.gui.tray.DesktopTrayItem;
import cn.oyzh.fx.gui.tray.QuitTrayItem;
import cn.oyzh.fx.gui.tray.SettingTrayItem;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.event.FXEventBus;
import cn.oyzh.fx.plus.event.FXEventConfig;
import cn.oyzh.fx.plus.ext.FXApplication;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.terminal.util.TerminalManager;
import cn.oyzh.i18n.I18nManager;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class EasyMongoApp extends FXApplication {

    /**
     * 项目信息
     */
    private static final Project PROJECT = Project.load();

    public static void main(String[] args) {
        try {
            // 开启fx的预览功能
            FXUtil.enablePreview();
            // 设置默认异常捕捉器
            Thread.setDefaultUncaughtExceptionHandler((t, ex) -> {
                if (!ExceptionUtil.hasMessage(ex, "isImageAutoSize")) {
                    ex.printStackTrace();
                    JulLog.error("thread:{} caught error:{}", t.getName(), ex.getMessage());
                }
            });
            SysConst.projectName(PROJECT.getName());
            SysConst.storeDir(MongoConst.getStorePath());
            SysConst.cacheDir(MongoConst.getCachePath());
            // 储存初始化
            MongoStoreUtil.init();
            // 系统图标
            FXConst.appIcon(MongoConst.ICON_PATH);
            // 事件总线
            EventFactory.registerEventBus(FXEventBus.class);
            EventFactory.syncEventConfig(FXEventConfig.SYNC);
            EventFactory.asyncEventConfig(FXEventConfig.ASYNC);
            EventFactory.defaultEventConfig(FXEventConfig.DEFAULT);
            if (JulLog.isInfoEnabled()) {
                JulLog.info("程序启动中...");
            }
            launch(EasyMongoApp.class, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void init() {
        try {
            // fx程序实例
            FXConst.INSTANCE = this;
            // 日志开始
            JulLog.info("{} init start.", SysConst.projectName());
            // 禁用fx的css日志
            FXUtil.disableCSSLogger();
            // 配置对象
            MongoSetting setting = MongoSettingStore.SETTING;
            // 应用区域
            I18nManager.apply(setting.getLocale());
            // 应用字体
            FontManager.apply(setting.fontConfig());
            // 应用主题
            ThemeManager.apply(setting.themeConfig());
            // 应用透明度
            OpacityManager.apply(setting.opacityConfig());
            // 注册异常处理器
            MessageBox.registerExceptionParser(MongoExceptionParser.INSTANCE);
            // 调用父类
            super.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            super.start(primaryStage);
            // 注册命令
            TerminalManager.setLoadHandler(MongoTerminalPane.TERMINAL_NAME, MongoTerminalManager::registerHandlers);
            // 开启定期gc
            SystemUtil.gcInterval(5_000);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("start error", ex);
        }
    }

    @Override
    protected void showMainView() {
        try {
            // 显示主页面
            StageManager.showStage(MainController.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("showMainView error", ex);
        }
    }

    @Override
    protected void initSystemTray() {
        try {
            if (!TrayManager.supported()) {
                JulLog.warn("tray is not supported.");
                return;
            }
            if (TrayManager.exist()) {
                return;
            }
            // 初始化
            TrayManager.init(MongoConst.TRAY_ICON_PATH);
            // 设置标题
            TrayManager.setTitle(PROJECT.getName() + " v" + PROJECT.getVersion());
            // 打开主页
            TrayManager.addMenuItem(new DesktopTrayItem( this::showMain));
            // 打开设置
            TrayManager.addMenuItem(new SettingTrayItem( this::showSetting));
            // 退出程序
            TrayManager.addMenuItem(new QuitTrayItem( () -> {
                JulLog.warn("exit app by tray.");
                StageManager.exit();
            }));
            // 鼠标事件
            TrayManager.onMouseClicked(e -> {
                // 单击鼠标主键，显示主页
                if (e.getButton() == MouseEvent.BUTTON1) {
                    this.showMain();
                }
            });
            // 显示托盘
            TrayManager.show();
        } catch (Exception ex) {
            JulLog.warn("不支持系统托盘!", ex);
        }
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(MainController.class);
            if (wrapper != null) {
                JulLog.info("front main.");
                wrapper.toFront();
            } else {
                JulLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        });
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(SettingController.class);
            if (wrapper != null) {
                JulLog.info("front setting.");
                wrapper.toFront();
            } else {
                JulLog.info("show setting.");
                StageManager.showStage(SettingController.class, StageManager.getPrimaryStage());
            }
        });
    }
}
