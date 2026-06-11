package cn.oyzh.easymongo.fx;


import cn.oyzh.fx.editor.incubator.control.JsonTextFiled;
import org.bson.types.Code;

/**
 * @author oyzh
 * @since 2024/7/21
 */
public class CodeTextFiled extends JsonTextFiled {

    @Override
    public Object getValue() {
        String text = this.getText();
        return new Code(text);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        this.setText(format(value));
    }

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
