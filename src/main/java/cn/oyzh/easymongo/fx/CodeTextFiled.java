package cn.oyzh.easymongo.fx;


import cn.oyzh.fx.editor.incubator.control.JsonTextFiled;
import javafx.scene.control.Skin;
import org.bson.types.Code;

/**
 * Code类型文本编辑框
 *
 * @author oyzh
 * @since 2024/7/21
 */
public class CodeTextFiled extends JsonTextFiled {

    @Override
    public CodeTextFiledSkin skin() {
        return (CodeTextFiledSkin) super.skin();
    }

    @Override
    protected CodeTextFiledSkin createDefaultSkin() {
        return new CodeTextFiledSkin(this);
    }

    @Override
    public void setArray(boolean array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getValue() {
        String text = this.getText();
        return new Code(text);
    }

    @Override
    public void formatValue() {
        this.setText(format(super.value()));
    }

    /**
     * 格式化Code值
     *
     * @param val 待格式化的值
     * @return 格式化后的字符串
     */
    public static String format(Object val) {
        if (val instanceof CharSequence sequence) {
            return sequence.toString();
        }
        if (val instanceof Code code) {
            return code.getCode();
        }
        return val == null ? null : val.toString();
    }

}
