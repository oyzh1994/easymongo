package cn.oyzh.easymongo.util;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * MongoDB 国际化辅助类
 *
 * @author oyzh
 * @since 2024/07/26
 */
public class MongoI18nHelper {

    /**
     * 获取首页欢迎语
     *
     * @return 欢迎语
     */
    public static String welcome() {
        return I18nResourceBundle.i18nString("mongo.home.welcome");
    }

    /**
     * 获取表格提示文本2
     *
     * @return 提示文本
     */
    public static String tableTip2() {
        return I18nResourceBundle.i18nString("db.table.tip2");
    }

    /**
     * 获取表格提示文本3
     *
     * @return 提示文本
     */
    public static String tableTip3() {
        return I18nResourceBundle.i18nString("db.table.tip3");
    }

    /**
     * 获取表格提示文本4
     *
     * @return 提示文本
     */
    public static String tableTip4() {
        return I18nResourceBundle.i18nString("db.table.tip4");
    }

}
