package cn.oyzh.easymongo.store;

import cn.oyzh.easymongo.domain.MongoSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class MongoSSHConfigStore extends JdbcStandardStore<MongoSSHConfig> {

    /**
     * 当前实例
     */
    public static final MongoSSHConfigStore INSTANCE = new MongoSSHConfigStore();

    public boolean replace(MongoSSHConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected Class<MongoSSHConfig> modelClass() {
        return MongoSSHConfig.class;
    }

    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    public MongoSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
