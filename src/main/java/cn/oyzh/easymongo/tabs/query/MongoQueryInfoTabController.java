package cn.oyzh.easymongo.tabs.query;

import cn.oyzh.easymongo.query.MongoQueryResult;
import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.text.area.FXTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * 查询信息标签页内容组件
 *
 * @author oyzh
 * @since 2024/08/12
 */
public class MongoQueryInfoTabController extends RichTabController {

    /**
     * 信息文本域
     */
    @FXML
    private FXTextArea infoArea;

    /**
     * 初始化查询结果信息
     *
     * @param results 查询结果集
     */
    public void init(MongoQueryResults<?> results) {
        this.infoArea.clear();
        if (results.isSuccess()) {
            for (MongoQueryResult result : results.getResults()) {
                this.infoArea.appendLine(result.getScript());
                if (result.isSuccess()) {
                    if (result.getUpdateCount() > 0) {
                        this.infoArea.appendLine("> Affected rows: " + result.getUpdateCount());
                    } else {
                        this.infoArea.appendLine("> OK");
                    }
                } else {
                    this.infoArea.appendLine("> " + result.getMsg());
                }
                this.infoArea.appendLine("> " + I18nHelper.time() + ": " + result.getUsedMs() + "ms");
                this.infoArea.appendLine("");
            }
        } else {
            this.infoArea.appendLine(results.getErrMsg());
        }
    }
}
