package cn.oyzh.easymongo.util;

import cn.oyzh.fx.gui.text.field.BitTextField;
import cn.oyzh.fx.gui.text.field.ChooseFileTextField;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.DateTextField;
import cn.oyzh.fx.gui.text.field.DateTimeTextField;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.DigitalTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.TimeTextField;
import cn.oyzh.fx.plus.controls.text.field.FXTextField;
import cn.oyzh.easymongo.mongo.MongoColumn;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

/**
 * db节点工具类
 *
 * @author oyzh
 * @since 2023/12/27
 */
public class MongoNodeUtil {

    public static Object getNodeVal(Node node) throws Exception {
        Object val = null;
        if (node instanceof TimeTextField node1) {
            val = node1.getValue();
        } else if (node instanceof DateTimeTextField node1) {
            val = node1.getValue();
        } else if (node instanceof DateTextField node1) {
            val = node1.getValue();
        } else if (node instanceof NumberTextField textField) {
            val = textField.getValue();
        } else if (node instanceof DecimalTextField textField) {
            val = textField.getValue();
        } else if (node instanceof BitTextField textField) {
            val = textField.getValue();
        } else if (node instanceof ChooseFileTextField textField) {
            val = textField.getData();
        } else if (node instanceof TextField textField) {
            val = textField.getText();
        } else if (node instanceof TextArea textArea) {
            val = textArea.getText();
        } else if (node instanceof ComboBox<?> comboBox) {
            val = comboBox.getSelectionModel().getSelectedItem();
        }
        return val;
    }

    public static void setNodeVal(Node node, Object val) {
        if (node == null || val == null) {
            return;
        }
        // if (node instanceof CalendarPicker<?> picker) {
        //     picker.setValue(val);
        // }
        if (node instanceof NumberTextField textField) {
            textField.setValue(val);
        } else if (node instanceof DecimalTextField textField) {
            textField.setValue(val);
        } else if (node instanceof BitTextField textField) {
            textField.setValue(val);
        } else if (node instanceof ChooseFileTextField textField) {
            textField.setData(val);
        } else if (node instanceof TextField textField) {
            textField.setText(val.toString());
        // } else if (node instanceof RichJsonTextAreaPane textAreaPane) {
        //     textAreaPane.setJsonStr(val.toString());
        // } else if (node instanceof RichTextAreaPane<?> textAreaPane) {
        //     textAreaPane.setText(val.toString());
        } else if (node instanceof TextArea textArea) {
            textArea.setText(val.toString());
        } else if (node instanceof ComboBox comboBox) {
            comboBox.getSelectionModel().select(val);
        }
    }

    public static Node generateNode(MongoColumn column) {
        return generateNode(column, true);
    }

    public static Node generateNode(MongoColumn column, boolean handlerDefaultValue) {
        Node node;
        if (column == null) {
            node = new FXTextField();
        } else if (column.supportString()) {
                node = new ClearableTextField();
        } else if (column.supportInteger()) {
            node = new DecimalTextField();
        } else {
            node = new ClearableTextField();
        }
        node.setId("value");
        return node;
    }

    /**
     * 处理小数位
     *
     * @param node          节点
     * @param decimalDigits 小数位
     */
    public static void handlerDigits(Node node, Integer decimalDigits) {
        // 设置小数位
        if (decimalDigits != null && decimalDigits > 0 && node instanceof DecimalTextField textField) {
            textField.setScaleLen(decimalDigits);
        }
    }

    /**
     * 处理注释
     *
     * @param node    节点
     * @param comment 注释
     */
    public static void handlerComment(Node node, String comment) {
        if (comment == null) {
            return;
        }
        if (node instanceof TextInputControl control) {
            control.setPromptText(comment);
        // } else if (node instanceof CalendarPicker<?> control) {
        //     control.setPromptText(comment);
        }
    }

    /**
     * 处理注释
     *
     * @param node         节点
     * @param defaultValue 默认值
     */
    public static void handlerDefaultValue(Node node, Object defaultValue) {
        if (defaultValue == null) {
            return;
        }
        if (node instanceof DigitalTextField field) {
            field.setValue(defaultValue);
        } else if (node instanceof ComboBox comboBox) {
            comboBox.getSelectionModel().select(defaultValue);
        } else if (node instanceof TextInputControl control) {
            control.setText(defaultValue.toString());
        // } else if (node instanceof CalendarPicker<?> picker) {
        //     if (defaultValue instanceof CharSequence sequence) {
        //         if (StrUtil.equalsAnyIgnoreCase(sequence, "CURRENT_TIMESTAMP")) {
        //             picker.setNow();
        //         }
        //     }
        }
    }

    /**
     * 处理注释
     *
     * @param node         节点
     * @param defaultValue 默认值
     */
    public static void handlerExampleValue(Node node, Object defaultValue) {
        if (defaultValue == null) {
            return;
        }
        if (node instanceof DigitalTextField field) {
            field.setValue(defaultValue);
        } else if (node instanceof ChooseFileTextField textField) {
            textField.setData(defaultValue);
        } else if (node instanceof TextInputControl control) {
            control.setText(defaultValue.toString());
        }
    }
}
