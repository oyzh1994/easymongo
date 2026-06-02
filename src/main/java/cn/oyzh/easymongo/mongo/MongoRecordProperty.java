package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.object.Destroyable;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.exception.MongoException;
import cn.oyzh.easymongo.util.MongoDataUtil;
import cn.oyzh.easymongo.util.MongoNodeUtil;
import cn.oyzh.easymongo.util.MongoRecordUtil;
import cn.oyzh.fx.plus.node.NodeDestroyUtil;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * db表记录属性
 *
 * @author oyzh
 * @since 2024/01/31
 */
public class MongoRecordProperty extends SimpleObjectProperty<Object> implements Destroyable {

    /**
     * 是否变更
     */
    private SimpleBooleanProperty changedProperty;

    /**
     * 表字段
     */
    private MongoColumn column;

    // /**
    //  * 表字段列表
    //  */
    // private MongoColumns columns;

    /**
     * 表记录
     */
    private MongoRecord record;

    /**
     * 原始数据
     */
    private Object original;

    /**
     * 设置为null标志位
     */
    private boolean setToNullFlag;

    /**
     * 只读模式
     */
    private final boolean readonly;

    public MongoRecordProperty(MongoRecord record, MongoColumn column, Object value, boolean readonly) {
        super(value);
        this.column = column;
        this.record = record;
        if (!readonly) {
            this.original = value;
        }
        this.readonly = readonly;
    }

    @Override
    public Object get() {
        if (this.readonly || !this.isChanged() || this.node == null) {
            return super.get();
        }
        if (this.setToNullFlag) {
            return null;
        }
        try {
            return MongoNodeUtil.getNodeVal(this.node);
        } catch (Exception ex) {
            throw new MongoException(ex);
        }
    }

    @Override
    public void set(Object newValue) {
        super.set(newValue);
        if (this.node != null) {
            MongoNodeUtil.setNodeVal(node, newValue);
        }
    }

    /**
     * 节点
     */
    private Node node;

    @Override
    public Object getValue() {
        //        if (this.readonly || !this.record.isEditable()) {
        if (this.readonly) {
            return MongoRecordUtil.formatValue(super.getValue(), this.column);
        }
        if (this.node == null) {
            this.node = MongoRecordUtil.getNode(this, super.get(), this.column);
            TableViewUtil.rowOnCtrlS(this.node);
            TableViewUtil.selectRowOnMouseClicked(this.node);
        }
        return this.node;
    }

    /**
     * 抛弃
     */
    public void discard() {
        if (this.isChanged() && this.node != null) {
            MongoNodeUtil.setNodeVal(this.node, super.get());
        }
        this.setChanged(false);
    }

    public SimpleBooleanProperty changedProperty() {
        if (this.changedProperty == null) {
            this.changedProperty = new SimpleBooleanProperty();
        }
        return this.changedProperty;
    }

    public boolean isChanged() {
        return this.changedProperty != null && this.changedProperty.get();
    }

    public void setChanged(boolean changed) {
        this.changedProperty().set(changed);
        DBStatusListener listener;
        listener = DBStatusListenerManager.getListener(this.column.getDbName() + ":" + this.column.getCollectionName());
        if (listener != null) {
            listener.changed(null, null, null);
        }
        this.setToNullFlag = false;
    }

    public void updateOriginal() {
        try {
            if (this.node != null) {
                super.set(MongoNodeUtil.getNodeVal(this.node));
                this.original = super.get();
            }
        } catch (Exception ex) {
            throw new MongoException(ex);
        }
    }

    public Node getControl() {
        return this.node;
    }

    public void vCopy() {
        ClipboardUtil.copy(this.node);
    }

    public void vPaste() {
        ClipboardUtil.paste(this.node);
    }

    /**
     * 复制为insert语句
     */
    public void vCopyAsInsertSql() {
        MongoColumns columns = this.record.getColumns();
        String sql = MongoDataUtil.toInsertSql(columns, this.record, true);
        ClipboardUtil.copy(sql);
    }

    /**
     * 复制为update语句
     */
    public void vCopyAsUpdateSql() {
        MongoColumns columns = this.record.getColumns();
        String sql = MongoDataUtil.toUpdateSql(columns, this.record);
        ClipboardUtil.copy(sql);
    }

    public void vSetToNull() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StringUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.clear();
            }
            textField.setPromptText(MongoRecordUtil.nullPromptText());
            NodeUtil.unFocus(this.node);
        }
        this.setToNullFlag = true;
    }

    public void vSetToEmptyString() {
        if (this.node instanceof TextField textField) {
            // 如果内容为空，则直接设置变更
            if (StringUtil.isEmpty(textField.getText())) {
                this.setChanged(true);
            } else {
                textField.setText("");
            }
            textField.setPromptText("");
            NodeUtil.unFocus(this.node);
        }
    }

    public MongoColumn getColumn() {
        return column;
    }

    public void setColumn(MongoColumn column) {
        this.column = column;
    }

    public Object getOriginal() {
        return original;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public synchronized void destroy() {
        if (this.node != null) {
            NodeDestroyUtil.destroyObject(this.node);
            this.node = null;
            this.column = null;
            this.record = null;
            this.original = null;
            this.changedProperty = null;
        }

    }
}
