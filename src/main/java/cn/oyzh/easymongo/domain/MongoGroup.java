package cn.oyzh.easymongo.domain;


import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.plus.domain.AppGroup;
import cn.oyzh.store.jdbc.Table;

/**
 * @author oyzh
 * @since 2023/12/15
 */
@Table("t_group")
public class MongoGroup extends AppGroup implements ObjectComparator<MongoGroup> {

    public MongoGroup() {
        super();
    }

    public MongoGroup(String name, String groupId, boolean expand) {
        super(name, groupId, expand);
    }

    @Override
    public boolean compare(MongoGroup t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.getName(), t1.getName());
    }
}
