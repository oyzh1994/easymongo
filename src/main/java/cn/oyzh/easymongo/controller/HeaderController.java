package cn.oyzh.easymongo.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.easymongo.controller.data.ShellMysqlDataTransportController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.easymongo.event.MongoEventUtil;
import javafx.fxml.FXML;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class HeaderController extends SubStageController {

    /**
     * 数据传输
     */
    @FXML
    private void transport() {
        StageAdapter wrapper = StageManager.getStage(ShellMysqlDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(ShellMysqlDataTransportController.class);
        }
    }

    /**
     * 设置
     */
    @FXML
    private void setting() {
        StageAdapter fxView = StageManager.getStage(SettingController.class);
        if (fxView != null) {
            fxView.toFront();
        } else {
            StageManager.showStage(SettingController.class, this.stage);
        }
    }

    /**
     * 关于
     */
    @FXML
    private void about() {
        StageManager.showStage(AboutController.class, this.stage);
    }

    /**
     * 退出
     */
    @FXML
    private void quit() {
        if (MessageBox.confirm(I18nHelper.quit() + " " + SysConst.projectName())) {
            StageManager.exit();
        }
    }

    /**
     * 工具箱
     */
    @FXML
    private void tool() {
    }

    /**
     * 布局1
     */
    @FXML
    private void layout1() {
        MongoEventUtil.layout1();
    }

    /**
     * 布局2
     */
    @FXML
    private void layout2() {
        MongoEventUtil.layout2();
    }

}
