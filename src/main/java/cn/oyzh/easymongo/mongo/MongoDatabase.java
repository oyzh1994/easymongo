package cn.oyzh.easymongo.mongo;

/**
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MongoDatabase {

    private String name;

    private Long sizeOnDisk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSizeOnDisk() {
        return sizeOnDisk;
    }

    public void setSizeOnDisk(Long sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
    }
}
