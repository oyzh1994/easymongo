package cn.oyzh.easymongo.data.dto;

import cn.oyzh.easymongo.mongo.MongoColumn;

/**
 * MongoDB 数据导出字段
 *
 * @author oyzh
 * @since 2024/8/27
 */
public class ShellMongoDataExportColumn extends MongoColumn {

    /**
     * 是否选中
     */
    private boolean selected = true;

    /**
     * 是否选中
     *
     * @return 是否选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置是否选中
     *
     * @param selected 是否选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
