package cn.oyzh.easymongo.script;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymongo.util.MongoUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MongoDB 脚本解析器，负责去除脚本注释并将脚本拆分为可逐条执行的语句
 *
 * @author oyzh
 * @since 2024/2/26
 */
public class MongoScriptParser {

    /** 待解析的脚本内容 */
    private final String scriptContent;

    /**
     * 构造脚本解析器
     *
     * @param scriptContent 待解析的脚本内容
     */
    public MongoScriptParser(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    /**
     * 移除脚本中的注释
     *
     * @return 去除注释后的脚本内容
     */
    public String removeComment() {
        return MongoUtil.removeComment(this.scriptContent);
    }
    //
    //    private Boolean single;
    //
    //    private Boolean select;
    //
    //    @Override
    //    public boolean isSingle() {
    //        if (this.single != null) {
    //            return this.single;
    //        }
    //        return false;
    //    }

    //    @Override
    //    public boolean isSelect() {
    //        if (this.select != null) {
    //            return this.select;
    //        }
    //        return false;
    //    }
    //
    //    @Override
    //    public boolean isFullColumn() {
    //        return false;
    //    }

    /**
     * 解析脚本，按分号将脚本拆分为多条可执行语句
     *
     * @return 脚本语句列表
     */
    public List<String> parseScript() {
        String sqlContent = this.removeComment();
        List<String> sqlList = new ArrayList<>();
        // druid无法解析这些语句，直接返回
        if (StringUtil.startWithAnyIgnoreCase(sqlContent,
                "show dbs",
                "show collections"
        )) {
            sqlList.add(sqlContent);
            return sqlList;
        }
        AtomicBoolean startFlag = new AtomicBoolean();
        StringBuilder sql = new StringBuilder();
        sqlContent.lines().forEach(l -> {
            if (l.startsWith("db.")) {
                startFlag.set(true);
            }
            if (startFlag.get()) {
                sql.append(l);
            }
            if (startFlag.get() && l.stripTrailing().endsWith(";")) {
                sqlList.add(sql.toString());
                sql.delete(0, sql.length());
                startFlag.set(false);
            }
        });
        if (!sql.isEmpty()) {
            sqlList.add(sql.toString());
        }
        //        this.single = null;
        //        this.select = null;
        return sqlList;
    }

    //    @Override
    //    public String parseSingleSql() throws Exception {
    //        String sql = this.removeComment();
    //        sql = sql.replace("\n", " ");
    //        return sql;
    //    }

    //    @Override
    //    public String prettySql() {
    //        return this.sqlContent;
    //    }

    /**
     * 创建脚本解析器
     *
     * @param script 脚本内容
     * @return 脚本解析器
     */
    public static MongoScriptParser getParser(String script) {
        return new MongoScriptParser(script);
    }
}
