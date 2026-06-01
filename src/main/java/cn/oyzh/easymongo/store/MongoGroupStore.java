package cn.oyzh.easymongo.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoGroup;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.param.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class MongoGroupStore extends JdbcStandardStore<MongoGroup> {

    /**
     * 当前实例
     */
    public static final MongoGroupStore INSTANCE = new MongoGroupStore();

    public List<MongoGroup> load() {
        return super.selectList();
    }

    public boolean replace(MongoGroup group) {
        if (group != null) {
            if (this.exist(group.getName()) || super.exist(group.getGid())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

    public boolean delete(String name) {
        if (StringUtil.isNotBlank(name)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("name", name));
            return this.delete(param);
        }
        return false;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public boolean exist(String name) {
        if (StringUtil.isNotBlank(name)) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<MongoGroup> modelClass() {
        return MongoGroup.class;
    }
}
