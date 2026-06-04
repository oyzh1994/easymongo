package cn.oyzh.easymongo.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.database.MongodbSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;


/**
 * db树连接值
 *
 * @author oyzh
 * @since 2023/12/22
 */
public class MongoConnectTreeItemValue extends RichTreeItemValue {

    public MongoConnectTreeItemValue(MongoConnectTreeItem item) {
        super(item);
    }

    @Override
    public MongoConnectTreeItem item() {
        return (MongoConnectTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (super.graphic() == null) {
            super.graphic(new MongodbSVGGlyph());
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
}
