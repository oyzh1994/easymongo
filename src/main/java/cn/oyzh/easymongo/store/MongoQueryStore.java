package cn.oyzh.easymongo.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.domain.MongoQuery;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.param.DeleteParam;
import cn.oyzh.store.jdbc.param.QueryParam;
import cn.oyzh.store.jdbc.param.SelectParam;

import java.util.List;

/**
 * zk查询存储
 *
 * @author oyzh
 * @since 2025/01/20
 */
public class MongoQueryStore extends JdbcStandardStore<MongoQuery> {

    /**
     * 当前实例
     */
    public static final MongoQueryStore INSTANCE = new MongoQueryStore();

    /**
     * 根据zk连接id加载列表
     *
     * @param iid    zk连接id
     * @param dbName db名称
     * @return 收藏列表
     */
    public List<MongoQuery> list(String iid, String dbName) {
        QueryParam param1 = QueryParam.of("iid", iid);
        QueryParam param2 = QueryParam.of("dbName", dbName);
        SelectParam selectParam = new SelectParam();
        selectParam.addQueryParam(param1);
        selectParam.addQueryParam(param2);
        return super.selectList(selectParam);
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(MongoQuery model) {
        if (model != null) {
            if (!this.exist(model.getUid())) {
                return this.insert(model);
            }
            return this.update(model);
        }
        return false;
    }

    /**
     * 根据zk连接id删除查询
     *
     * @param iid zk连接id
     * @return 结果
     */
    public boolean deleteByIid(String iid) {
        if (StringUtil.isEmpty(iid)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(QueryParam.of("iid", iid));
            return this.delete(param);
        }
        return false;
    }

    @Override
    protected Class<MongoQuery> modelClass() {
        return MongoQuery.class;
    }
}
