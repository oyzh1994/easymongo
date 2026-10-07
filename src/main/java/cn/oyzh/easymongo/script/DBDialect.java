package cn.oyzh.easymongo.script;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 数据库类型(方言)
 *
 * @author oyzh
 * @since 2024/2/20
 */
public enum DBDialect {

    /** MySQL 数据库 */
    MYSQL;


    /**
     * 获取全部数据库方言
     *
     * @return 数据库方言列表
     */
    public static List<DBDialect> valueList() {
        List<DBDialect> list = new ArrayList<>();
        Collections.addAll(list, values());
        return list;
    }



}
