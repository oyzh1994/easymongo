package cn.oyzh.easymongo.util;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.mongo.MongoRecordProperty;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.DecimalTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.text.field.FXTextField;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.font.FontUtil;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.util.ControlUtil;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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
        if (column.supportInteger()) {
            NumberTextField textField = new NumberTextField();
            textField.setValue(object);
            textField.setBackground(ControlUtil.background(Color.valueOf("#80D06A")));
            node = textField;
        } else if (column.supportDigits()) {
            DecimalTextField textField = new DecimalTextField();
            textField.setValue(object);
            textField.setBackground(ControlUtil.background(Color.valueOf("#60C6F4")));
            node = textField;
        } else {
            FXTextField textField = new FXTextField();
            if (column.is_id()) {
                textField.setEditable(false);
            } else {
                textField.setBackground(ControlUtil.background(Color.valueOf("#FA7B73")));
            }
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

    /**
     * 计算合适的字段宽
     *
     * @param column 字段
     * @return 结果
     */
    public static double suitableColumnWidth(MongoColumn column) {
        if (column.is_id()) {
            return FontUtil.textWidth("a".repeat(32), FontManager.currentFont());
        }
        String str1 = column.getName();
        String str2 = column.getType();
        double w1 = FontUtil.textWidth(str1, FontManager.currentFont());
        double w2 = FontUtil.textWidth(str2, FontManager.currentFont());
        double w3 = Math.max(w1, w2);
        return w3 + 50;
    }

    public static ContextMenu getColumnContextMenu(MongoRecordProperty property) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(getColumnMenuItem(property));
        return contextMenu;
    }

    public static List<FXMenuItem> getColumnMenuItem(MongoRecordProperty property) {
        List<FXMenuItem> menuItems = new ArrayList<>();
        if (property.getColumn().is_id()) {
            FXMenuItem copy = MenuItemHelper.copy(property::vCopy);
            menuItems.add(copy);
            FXMenuItem edit = MenuItemHelper.edit(property::vEdit);
            menuItems.add(edit);
            FXMenuItem copyAsInsertStatement = MenuItemHelper.copyAsInsertStatement(property::vCopyAsInsertSql);
            menuItems.add(copyAsInsertStatement);
            FXMenuItem copyAsUpdateStatement = MenuItemHelper.copyAsUpdateStatement(property::vCopyAsUpdateSql);
            menuItems.add(copyAsUpdateStatement);
        } else {
            FXMenuItem copy = MenuItemHelper.copy(property::vCopy);
            menuItems.add(copy);
            FXMenuItem paste = MenuItemHelper.paste(property::vPaste);
            menuItems.add(paste);
            FXMenuItem edit = MenuItemHelper.edit(property::vEdit);
            menuItems.add(edit);
            FXMenuItem setToNull = MenuItemHelper.setToNull(property::vSetToNull);
            menuItems.add(setToNull);
            FXMenuItem setToEmptyString = MenuItemHelper.setToEmptyString(property::vSetToEmptyString);
            menuItems.add(setToEmptyString);
            FXMenuItem copyAsInsertStatement = MenuItemHelper.copyAsInsertStatement(property::vCopyAsInsertSql);
            menuItems.add(copyAsInsertStatement);
            FXMenuItem copyAsUpdateStatement = MenuItemHelper.copyAsUpdateStatement(property::vCopyAsUpdateSql);
            menuItems.add(copyAsUpdateStatement);
        }
        return menuItems;
    }

    public static MongoRecord docToRecord(String doc, String dbName, String collectionName) {
        JSONObject object = JSONObject.parseObject(doc);
        MongoColumns columns = new MongoColumns();
        for (String col : object.keySet()) {
            MongoColumn column = new MongoColumn(col);
            column.setDbName(dbName);
            column.setCollectionName(collectionName);
            columns.add(column);
        }
        MongoRecord record = new MongoRecord(columns);
        for (MongoColumn column : columns) {
            Object value = object.get(column.getName());
            record.putValue(column, value);
            column.setType(MongoUtil.getType(value));
        }
        return record;
    }
}
