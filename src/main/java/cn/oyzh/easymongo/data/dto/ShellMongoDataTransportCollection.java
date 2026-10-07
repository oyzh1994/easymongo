package cn.oyzh.easymongo.data.dto;

/**
 * MongoDB 数据传输集合
 *
 * @author oyzh
 * @since 2024-09-06
 */
public class ShellMongoDataTransportCollection {

    /**
     * 表名称
     */
    private String name;

    /**
     * 是否选中
     */
    private boolean selected = true;

    /**
     * 获取表名称
     *
     * @return 表名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置表名称
     *
     * @param name 表名称
     */
    public void setName(String name) {
        this.name = name;
    }

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
