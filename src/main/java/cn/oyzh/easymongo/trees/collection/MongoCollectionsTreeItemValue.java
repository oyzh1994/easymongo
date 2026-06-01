package cn.oyzh.easymongo.trees.collection;

import cn.oyzh.fx.gui.svg.glyph.database.TableSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.paint.Color;

/**
 * db树表类型值
 *
 * @author oyzh
 * @since 2023/12/08
 */
public class MongoCollectionsTreeItemValue extends RichTreeItemValue {

    public MongoCollectionsTreeItemValue(MongoCollectionsTreeItem item) {
        super(item);
    }

    @Override
    public MongoCollectionsTreeItem item() {
        return (MongoCollectionsTreeItem) super.item();
    }

    @Override
    public String name() {
        return I18nHelper.collections();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new TableSVGGlyph());
            super.graphic().disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item().isChildEmpty()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }

    @Override
    public String extra() {
        return super.extra();
    }

    @Override
    public Color extraColor() {
        return Color.valueOf("#228B22");
    }
}
