package cn.oyzh.easymongo.controller.collection;

import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoDataUtil;
import cn.oyzh.fx.editor.incubator.Editor;
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
 * 添加记录库业务
 *
 * @author oyzh
 * @since 2026/06/03
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        stageStyle = FXStageStyle.EXTENDED,
        value = FXConst.FXML_PATH + "record/mongoDocumentUpdate.fxml"
)
public class MongoDocumentUpdateController extends StageController {

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
            //            if (JSONUtil.isJson(doc)) {
            this.setProp("doc", doc);
            this.closeWindow();
            //            } else {
            //                MessageBox.warn(I18nHelper.documentInvalid());
            //            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.updateDocument();
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        MongoRecord record = this.getProp("record");
        //        JSONObject object = new JSONObject();
        //        for (MongoColumn column : record.getColumns()) {
        //            if (column.is_id()) {
        //                continue;
        //            }
        //            object.put(column.getName(), record.getValue(column.getName()));
        //        }
        //        String json = JSONUtil.toPretty(object);
        String text = MongoDataUtil.getRecordScript(record);
        this.doc.setText(text);
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }
}
