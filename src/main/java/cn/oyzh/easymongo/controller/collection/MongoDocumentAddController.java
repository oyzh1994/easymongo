package cn.oyzh.easymongo.controller.collection;

import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.fx.editor.incubator.Editor;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 添加记录库业务
 *
 * @author oyzh
 * @since 2026/06/03
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        value = FXConst.FXML_PATH + "record/mongoDocumentAdd.fxml"
)
public class MongoDocumentAddController extends StageController {

    /**
     * 文档
     */
    @FXML
    private Editor doc;

    /**
     * 添加db库
     */
    @FXML
    private void add() {
        try {
            // 检查字段是否存在
            String doc = this.doc.getText();
            if (JSONUtil.isJson(doc)) {
                this.setProp("doc", doc);
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.documentInvalid());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.addDocument();
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.doc.setText("""
                {
                
                }""");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }
}
