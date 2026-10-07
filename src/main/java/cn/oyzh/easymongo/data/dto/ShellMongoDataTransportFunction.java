package cn.oyzh.easymongo.data.dto;

/**
 * MongoDB 数据传输函数
 *
 * @author oyzh
 * @since 2024-09-06
 */
public class ShellMongoDataTransportFunction {

    /**
     * 函数名称
     */
    private String name;

    /**
     * 是否选中
     */
    private boolean selected = true;

    /**
     * 获取函数名称
     *
     * @return 函数名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置函数名称
     *
     * @param name 函数名称
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
