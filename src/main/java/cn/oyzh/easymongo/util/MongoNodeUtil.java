package cn.oyzh.easymongo.util;

import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.text.field.FXTextField;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * db节点工具类
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoNodeUtil {

    public static Object getNodeVal(Node node)   {
        Object val = null;
        if (node instanceof NumberTextField textField) {
            val = textField.getValue();
        } else if (node instanceof DecimalTextField textField) {
            val = textField.getValue();
        } else if (node instanceof TextField textField) {
            val = textField.getText();
        }
        return val;
    }

    public static void setNodeVal(Node node, Object val) {
        if (node == null || val == null) {
            return;
        }
        if (node instanceof NumberTextField textField) {
            textField.setValue(val);
        } else if (node instanceof TextField textField) {
            textField.setText(val.toString());
        }
    }

    public static Node generateNode(MongoColumn column) {
        Node node;
        if (column.supportInteger()) {
            node = new NumberTextField();
        } else if (column.supportDigits()) {
            node = new DecimalTextField();
        } else {
            node = new FXTextField();
        }
        node.setId("value");
        return node;
    }
}
