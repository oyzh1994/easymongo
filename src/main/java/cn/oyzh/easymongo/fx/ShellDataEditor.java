package cn.oyzh.easymongo.fx;

import cn.oyzh.easymongo.domain.MongoSetting;
import cn.oyzh.easymongo.store.MongoSettingStore;
import cn.oyzh.fx.editor.incubator.Editor;
import cn.oyzh.fx.plus.font.FontManager;
import javafx.scene.text.Font;

/**
 * @author oyzh
 * @since 2025-03-26
 */
public class ShellDataEditor extends Editor {

    @Override
    protected Font getEditorFont() {
        if (super.getEditorFont() == null) {
            MongoSetting setting = MongoSettingStore.SETTING;
            Font font = FontManager.toFont(setting.editorFontConfig());
            super.setEditorFont(font);
        }
        return super.getEditorFont();
    }
}
