package cn.oyzh.easymongo.controller.document;

import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.fx.editor.incubator.control.JsonEditor;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
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
        stageStyle = FXStageStyle.EXTENDED,
        value = FXConst.FXML_PATH + "document/mongoBucketDocumentUpdate.fxml"
)
public class MongoBucketDocumentUpdateController extends StageController {

    /**
     * 文件名
     */
    @FXML
    private ClearableTextField filename;

    /**
     * 内容类型
     */
    @FXML
    private ClearableTextField contentType;

    /**
     * 元数据
     */
    @FXML
    private JsonEditor metadata;

    /**
     * 数据
     */
    private MongoRecord record;

    /**
     * 添加db信息
     */
    @FXML
    private void update() {
        try {
            String filename = this.filename.getTextTrim();
            String metadata = this.metadata.getTextTrim();
            String contentType = this.contentType.getTextTrim();
            if (metadata.isBlank()) {
                metadata = null;
            }
            MongoRecord record = new MongoRecord(this.record.getColumns());
            record.putValue(MongoUtil.ID, this.record._idValue());
            record.putValue("filename", filename);
            record.putValue("metadata", metadata);
            record.putValue("contentType", contentType);
            this.setProp("document", record);
            this.closeWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.record = this.removeProp("document");
        this.filename.setText((String) this.record.getValue("filename"));
        this.metadata.setText((String) this.record.getValue("metadata"));
        this.contentType.setText((String) this.record.getValue("contentType"));
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.updateDocument();
    }
}
