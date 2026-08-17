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
        MongoSetting setting = MongoSettingStore.SETTING;
        return FontManager.toFont(setting.editorFontConfig());
    }
}
