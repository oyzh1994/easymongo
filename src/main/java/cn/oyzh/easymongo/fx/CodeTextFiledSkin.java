package cn.oyzh.easymongo.fx;

import cn.oyzh.fx.editor.incubator.EditorFormatType;
import cn.oyzh.fx.editor.incubator.control.LongTextFiledSkin;
import javafx.scene.control.TextField;

/**
 * Code类型文本编辑框皮肤
 *
 * @author oyzh
 * @since 2026-06-11
 */
public class CodeTextFiledSkin extends LongTextFiledSkin {

    /**
     * 构造皮肤
     *
     * @param textField 文本编辑框
     */
    public CodeTextFiledSkin(TextField textField) {
        super(textField);
    }

    @Override
    protected EditorFormatType getFormatType() {
        return EditorFormatType.SQL;
    }
}
