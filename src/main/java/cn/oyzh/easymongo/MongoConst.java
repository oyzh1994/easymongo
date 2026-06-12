package cn.oyzh.easymongo;


import cn.oyzh.common.system.SystemUtil;
import cn.oyzh.common.util.JarUtil;

import java.io.File;

/**
 * db常量对象
 *
 * @author oyzh
 * @since 2023/06/16
 */
public class MongoConst {

//    /**
//     * 数据保存路径
//     */
//    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easymongo" + File.separator;
//
//    /**
//     * 缓存保存路径
//     */
//    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/db_clip.png";

    /**
     * 托盘icon地址
     */
    public final static String TRAY_ICON_PATH = "/image/db_clip.png";


    /**
     * 获取存储路径
     *
     * @return 存储路径
     */
    public static String getStorePath() {
        if (JarUtil.isInJar()) {
            return SystemUtil.userHome() + File.separator + ".easymongo" + File.separator;
        }
        return SystemUtil.userHome() + File.separator + ".easymongo_dev" + File.separator;
    }

    /**
     * 获取缓存路径
     *
     * @return 缓存路径
     */
    public static String getCachePath() {
        return getStorePath() + "cache" + File.separator;
    }

}
