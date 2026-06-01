package cn.oyzh.easymongo.store;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymongo.domain.MongoSetting;
import cn.oyzh.store.jdbc.JdbcKeyValueStore;


/**
 * db设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
public class MongoSettingStore extends JdbcKeyValueStore<MongoSetting> {

    /**
     * 当前实例
     */
    public static final MongoSettingStore INSTANCE = new MongoSettingStore();

    /**
     * 当前设置
     */
    public static final MongoSetting SETTING = INSTANCE.load();

    public MongoSetting load() {
        MongoSetting setting = null;
        try {
            setting = super.select();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("load setting error", ex);
        }
        if (setting == null) {
            setting = new MongoSetting();
        }
        return setting;
    }

    public boolean replace(MongoSetting model) {
        if (model != null) {
            return this.update(model);
        }
        return false;
    }

    @Override
    protected Class<MongoSetting> modelClass() {
        return MongoSetting.class;
    }
}
