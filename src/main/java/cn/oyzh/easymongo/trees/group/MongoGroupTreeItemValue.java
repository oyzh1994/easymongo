package cn.oyzh.easymongo.trees.group;

import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * db树 Group节点值
 *
 * @author oyzh
 * @since 2023/12/21
 */
public class MongoGroupTreeItemValue extends RichTreeItemValue {

    public MongoGroupTreeItemValue(MongoGroupTreeItem item) {
        super(item);
    }

    @Override
    public MongoGroupTreeItem item() {
        return (MongoGroupTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new GroupSVGGlyph());
            super.graphic().disableTheme();
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (!this.item().isChildEmpty()) {
           return Color.DEEPSKYBLUE;
        }
        return super.graphicColor();
    }
}
