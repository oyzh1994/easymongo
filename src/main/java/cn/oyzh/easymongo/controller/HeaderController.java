package cn.oyzh.easymongo.controller;

import cn.oyzh.common.SysConst;
import cn.oyzh.easymongo.controller.data.ShellMongoDataTransportController;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.gui.svg.pane.LayoutSVGPane;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.easymongo.event.MongoEventUtil;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

/**
 * 主页头部业务
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class HeaderController extends SubStageController {

    /**
     * 布局组件
     */
    @FXML
    private LayoutSVGPane layoutPane;

    /**
     * 布局
     */
    @FXML
    private void layout() {
        if (this.layoutPane.isLayout1()) {
            MongoEventUtil.layout2();
        } else {
            MongoEventUtil.layout1();
        }
    }

    /**
     * 数据传输
     */
    @FXML
    private void transport() {
        StageAdapter wrapper = StageManager.getStage(ShellMongoDataTransportController.class);
        if (wrapper != null) {
            wrapper.toFront();
        } else {
            StageManager.showStage(ShellMongoDataTransportController.class);
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
     * 布局1事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void layout1(Layout1Event event) {
        this.layoutPane.setTipText(I18nHelper.showLeftSide());
        this.layoutPane.layout1();
    }

    /**
     * 布局2事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void layout2(Layout2Event event) {
        this.layoutPane.setTipText(I18nHelper.hiddenLeftSide());
        this.layoutPane.layout2();
    }

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        this.layoutPane.setTipText(I18nHelper.hiddenLeftSide());
    }
}
