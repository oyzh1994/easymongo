package cn.oyzh.easymongo.store;

import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.domain.MongoSSHConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class MongoConnectStore extends JdbcStandardStore<MongoConnect> {

    /**
     * 当前实例
     */
    public static final MongoConnectStore INSTANCE = new MongoConnectStore();

    /**
     * ssh配置存储
     */
    private final MongoSSHConfigStore sshConfigStore = MongoSSHConfigStore.INSTANCE;

    /**
     * 加载列表
     *
     * @return redis连接列表
     */
    public List<MongoConnect> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(MongoConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }

            // ssh处理
            MongoSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(model.getId());
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }
        }
        return result;
    }

    @Override
    public boolean delete(MongoConnect model) {
        boolean result = super.delete(model);
        // 删除关联配置
        if (result) {
            this.sshConfigStore.deleteByIid(model.getId());
        }
        return result;
    }

    @Override
    protected Class<MongoConnect> modelClass() {
        return MongoConnect.class;
    }
}
