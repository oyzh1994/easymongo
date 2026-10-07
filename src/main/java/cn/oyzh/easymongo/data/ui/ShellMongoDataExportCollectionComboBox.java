package cn.oyzh.easymongo.data.ui;

import cn.oyzh.easymongo.data.dto.ShellMongoDataExportCollection;
import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.converter.SimpleStringConverter;

/**
 * 数据导出集合下拉框，用于选择需要导出的集合
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class ShellMongoDataExportCollectionComboBox extends FXComboBox<ShellMongoDataExportCollection> {

    @Override
    public void initNode(){
        this.setConverter(new SimpleStringConverter<>() {
            @Override
            public String toString(ShellMongoDataExportCollection object) {
                if (object != null) {
                    return object.getName();
                }
                return null;
            }
        });
        super.initNode();
    }
}
