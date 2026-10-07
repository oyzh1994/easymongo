package cn.oyzh.easymongo.store;

import cn.oyzh.easymongo.domain.MongoSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.param.QueryParam;

/**
 * MongoDB连接的ssh配置存储
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class MongoSSHConfigStore extends JdbcStandardStore<MongoSSHConfig> {

    /**
     * 当前实例
     */
    public static final MongoSSHConfigStore INSTANCE = new MongoSSHConfigStore();

    /**
     * 替换ssh配置，存在则更新，否则新增
     *
     * @param model ssh配置
     * @return 结果
     */
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

    /**
     * 根据连接id删除ssh配置
     *
     * @param iid 连接id
     */
    public void deleteByIid(String iid) {
        DeleteParam param = new DeleteParam();
        param.addQueryParam(QueryParam.of("iid", iid));
        super.delete(param);
    }

    /**
     * 根据连接id获取ssh配置
     *
     * @param iid 连接id
     * @return ssh配置
     */
    public MongoSSHConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
