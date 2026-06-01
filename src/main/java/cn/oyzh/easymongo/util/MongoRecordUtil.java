package cn.oyzh.easymongo.util;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoRecordProperty;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.font.FontUtil;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/7/17
 */
public class MongoRecordUtil {

    public static Node getNode(MongoRecordProperty property, Object object, MongoColumn column) {
        Node node;
        String columnType = column.getType();
         if (column.supportInteger()) {
            NumberTextField textField = new NumberTextField();
            textField.setValue(object);
            node = textField;
        } else {
            ClearableTextField textField = new ClearableTextField();
            textField.setValue(object);
            node = textField;
        }
        if (node instanceof TextField textField) {
            if (object == null) {
                textField.setPromptText(nullPromptText());
            }
            textField.setContextMenu(getColumnContextMenu(property));
            textField.textProperty().addListener((observable, oldValue, newValue) -> property.setChanged(true));
        }
        return node;
    }

    public static String formatValue(Object object, MongoColumn column) {
        String val = null;
        String columnType = column.getType();
        if (StringUtil.isBlank(columnType)) {
            if (object instanceof CharSequence sequence) {
                val = sequence.toString();
            } else if (object instanceof byte[] bytes) {
                val = new String(bytes);
            } else if (object instanceof Date date) {
                val = date.toString();
            } else if (object != null) {
                val = object.toString();
            }
        } else if (column.supportInteger()) {
            val = NumberTextField.format(object);
        } else if (column.supportDigits()) {
            val = DecimalTextField.format(object);
        } else if (column.supportString()) {
            val = ClearableTextField.format(object);
        } else {
            val = ClearableTextField.format(object);
        }
        return val;
    }

    public static String nullPromptText() {
        return "(Null)";
    }

    // public static double suitableColumnWidth(String columnType) {
    //     if (DBColumnUtil.isGeometryType(columnType)) {
    //         return 120;
    //     }
    //     if (DBColumnUtil.isPointType(columnType)) {
    //         return 110;
    //     }
    //     if (DBColumnUtil.isMultiPointType(columnType)) {
    //         return 200;
    //     }
    //     if (DBColumnUtil.isPolygonType(columnType)) {
    //         return 220;
    //     }
    //     if (DBColumnUtil.isMultiPolygonType(columnType)) {
    //         return 420;
    //     }
    //     if (DBColumnUtil.isLineStringType(columnType)) {
    //         return 180;
    //     }
    //     if (DBColumnUtil.isMultiLineStringType(columnType)) {
    //         return 320;
    //     }
    //     if (DBColumnUtil.isGeomCollectionType(columnType)) {
    //         return 600;
    //     }
    //     if (DBColumnUtil.isYearType(columnType)) {
    //         return 80;
    //     }
    //     if (DBColumnUtil.supportJson(columnType)) {
    //         return 150;
    //     }
    //     if (DBColumnUtil.supportTimestamp(columnType)) {
    //         return 160;
    //     }
    //     if (DBColumnUtil.supportBinary(columnType)) {
    //         return 140;
    //     }
    //     if (DBColumnUtil.isDateType(columnType)) {
    //         return 110;
    //     }
    //     return 100;
    // }

    /**
     * 计算合适的字段宽
     *
     * @param column 字段
     * @return 结果
     */
    public static double suitableColumnWidth(MongoColumn column) {
        double w1 = FontUtil.textWidth(column.getName());
        double w2;
            w2 = FontUtil.textWidth(column.getType());
        double w3 = Math.max(w1, w2);
        return w3 + 30;
    }

    public static ContextMenu getColumnContextMenu(MongoRecordProperty property) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(getColumnMenuItem(property));
        return contextMenu;
    }

    public static List<FXMenuItem> getColumnMenuItem(MongoRecordProperty property) {
        List<FXMenuItem> menuItems = new ArrayList<>();
        FXMenuItem copy = MenuItemHelper.copy(property::vCopy);
        menuItems.add(copy);
        FXMenuItem paste = MenuItemHelper.paste(property::vPaste);
        menuItems.add(paste);
        // FXMenuItem delete = MenuItemHelper.deleteRecord(property::vDelete);
        FXMenuItem setToNull = MenuItemHelper.setToNull(property::vSetToNull);
        menuItems.add(setToNull);
        FXMenuItem setToEmptyString = MenuItemHelper.setToEmptyString(property::vSetToEmptyString);
        menuItems.add(setToEmptyString);
        FXMenuItem copyAsInsertStatement = MenuItemHelper.copyAsInsertStatement(property::vCopyAsInsertSql);
        menuItems.add(copyAsInsertStatement);
        FXMenuItem copyAsUpdateStatement = MenuItemHelper.copyAsUpdateStatement(property::vCopyAsUpdateSql);
        menuItems.add(copyAsUpdateStatement);
        // menuItems.add(delete);
        return menuItems;
    }
}
