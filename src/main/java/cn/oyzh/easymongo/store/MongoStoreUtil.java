package cn.oyzh.easymongo.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.easymongo.MongoConst;
import cn.oyzh.store.jdbc.JdbcConst;
import cn.oyzh.store.jdbc.JdbcDialect;
import cn.oyzh.store.jdbc.JdbcManager;

/**
 * @author oyzh
 * @since 2024-09-23
 */
public class MongoStoreUtil {

    /**
     * 执行初始化
     */
    public static void init() {
        JdbcConst.dbCacheSize(65535);
        JdbcConst.dbPageSize(1024);
        JdbcConst.dbDialect(JdbcDialect.H2);
        JdbcConst.dbFile(MongoConst.getStorePath() + "db");
        try {
            JdbcManager.takeoff();
        } catch (Exception ex) {
            if (StringUtil.containsAny(ex.getMessage(), "Database may be already in use")) {
                MessageBox.warn(I18nHelper.programTip1());
            }
        }
    }
}
