package cn.oyzh.easymongo.mongo;

/**
 * MongoDB 数据库
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MongoDatabase {

    /**
     * 数据库名称
     */
    private String name;

    /**
     * 磁盘占用大小
     */
    private Double sizeOnDisk;

    /**
     * 获取数据库名称
     *
     * @return 数据库名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置数据库名称
     *
     * @param name 数据库名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取磁盘占用大小
     *
     * @return 磁盘占用大小
     */
    public Double getSizeOnDisk() {
        return sizeOnDisk;
    }

    /**
     * 设置磁盘占用大小
     *
     * @param sizeOnDisk 磁盘占用大小
     */
    public void setSizeOnDisk(Double sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
    }
}
