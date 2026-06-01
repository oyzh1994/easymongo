package cn.oyzh.easymongo;


import java.io.File;

/**
 * db常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class MongoConst {

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easymongo" + File.separator;

    /**
     * 缓存保存路径
     */
    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/db_clip.png";

    /**
     * 托盘icon地址
     */
    public final static String TRAY_ICON_PATH = "/image/db_clip.png";

}
