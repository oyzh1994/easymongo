package cn.oyzh.easymongo.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.fx.MonogoAuthMethodComboBox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoGroup;
import cn.oyzh.easymongo.domain.MongoSSHConfig;
import cn.oyzh.easymongo.event.MongoEventUtil;
import cn.oyzh.easymongo.store.MongoConnectStore;
import cn.oyzh.easymongo.util.MongoConnectUtil;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 添加db信息业务
 *
 * @author oyzh
 * @since 2023/12/22
 */
@StageAttribute(
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "connect/mongoConnectAdd.fxml"
)
public class MongoConnectAddController extends StageController {

    /**
     * tab组件
     */
    @FXML
    private FXTabPane tabPane;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 用户名
     */
    @FXML
    private ClearableTextField user;

    /**
     * 认证密码
     */
    @FXML
    private ClearableTextField password;

    /**
     * 备注
     */
    @FXML
    private FXTextArea remark;

    /**
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 认证方式
     */
    @FXML
    private MonogoAuthMethodComboBox authMethod;

    /**
     * 认证数据库
     */
    @FXML
    private ClearableTextField authDatabase;

    /**
     * 连接超时
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * ssh面板
     */
    @FXML
    private FXTab sshTab;

    /**
     * 开启ssh
     */
    @FXML
    private FXToggleSwitch sshForward;

    /**
     * ssh主机地址
     */
    @FXML
    private ClearableTextField sshHost;

    /**
     * ssh主机端口
     */
    @FXML
    private PortTextField sshPort;

    /**
     * ssh主机端口
     */
    @FXML
    private NumberTextField sshTimeout;

    /**
     * ssh主机用户
     */
    @FXML
    private ClearableTextField sshUser;

    /**
     * ssh主机密码
     */
    @FXML
    private ClearableTextField sshPassword;

    /**
     * 分组
     */
    private MongoGroup group;

    /**
     * db连接储存对象
     */
    private final MongoConnectStore connectStore = MongoConnectStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (!this.hostPort.validate()) {
            this.tabPane.select(0);
            return null;
        }
        if (!this.hostIp.validate()) {
            this.tabPane.select(0);
            return null;
        }
        hostText = hostIp + ":" + this.hostPort.getValue();
        return hostText;
    }

    /**
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private MongoSSHConfig getSSHConfig() {
        MongoSSHConfig sshConfig = new MongoSSHConfig();
        sshConfig.setHost(this.sshHost.getText());
        sshConfig.setUser(this.sshUser.getText());
        sshConfig.setPort(this.sshPort.getIntValue());
        sshConfig.setPassword(this.sshPassword.getText());
        sshConfig.setTimeout(this.sshTimeout.getIntValue());
        return sshConfig;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StringUtil.isBlank(host) || StringUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        } else {
            // 创建redis连接
            MongoConnect redisConnect = new MongoConnect();
            redisConnect.setHost(host);
            redisConnect.setConnectTimeOut(3);
            redisConnect.setUser(this.user.getText());
            redisConnect.setPassword(this.password.getText());
            redisConnect.setSshForward(this.sshForward.isSelected());
            if (redisConnect.isSSHForward()) {
                redisConnect.setSshConfig(this.getSSHConfig());
            }
            MongoConnectUtil.testConnect(this.stage, redisConnect);
        }
    }

    /**
     * 添加db信息
     */
    @FXML
    private void add() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StringUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            String authType = this.authMethod.getType();
            String authDatabase = this.authDatabase.getTextTrim();
            MongoConnect mysqlConnect = new MongoConnect();
            mysqlConnect.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            mysqlConnect.setHost(host);
            mysqlConnect.setAuthType(authType);
            mysqlConnect.setAuthDatabase(authDatabase);
            mysqlConnect.setUser(this.user.getText());
            mysqlConnect.setRemark(this.remark.getTextTrim());
            mysqlConnect.setPassword(this.password.getText());
            mysqlConnect.setPassword(this.password.getText());
            mysqlConnect.setGroupId(this.group == null ? null : this.group.getGid());
            mysqlConnect.setConnectTimeOut(connectTimeOut == null ? 5 : connectTimeOut.intValue());
            mysqlConnect.setSshConfig(this.getSSHConfig());
            mysqlConnect.setSshForward(this.sshForward.isSelected());

            // 保存数据
            boolean result = this.connectStore.replace(mysqlConnect);
            if (result) {
                MongoEventUtil.connectAdded(mysqlConnect);
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.sshTab, "ssh");
            } else {
                NodeGroupUtil.disable(this.sshTab, "ssh");
            }
        });
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.group = this.getProp("group");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.connectAddTitle();
    }
}
