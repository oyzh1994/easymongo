package cn.oyzh.easymongo.query;

import cn.oyzh.common.thread.ThreadUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author oyzh
 * @since 2024/2/21
 */
public class MysqlQueryUtil {

    /**
     * 0未初始化
     * 1 初始化中
     * 2 已初始化
     */
    private static int indexStatus = 0;

    /**
     * 关键字
     */
    private static final List<String> DB_KEYWORDS = new ArrayList<>();

    static {
        // dml
        DB_KEYWORDS.add("SELECT");
        DB_KEYWORDS.add("UPDATE");
        DB_KEYWORDS.add("DELETE");
        DB_KEYWORDS.add("FROM");
        DB_KEYWORDS.add("WHERE");
        DB_KEYWORDS.add("WHEN");
        DB_KEYWORDS.add("INSERT INTO");

        // ddl
        DB_KEYWORDS.add("DATABASE");
        DB_KEYWORDS.add("DATABASES");
        DB_KEYWORDS.add("SHOW");
        DB_KEYWORDS.add("ALTER TABLE");
        DB_KEYWORDS.add("CREATE TABLE");
        DB_KEYWORDS.add("DROP TABLE");
        DB_KEYWORDS.add("CHANGE");
        DB_KEYWORDS.add("COLUMN");
        DB_KEYWORDS.add("CHARACTER SET");
        DB_KEYWORDS.add("COMMENT");
        DB_KEYWORDS.add("FIRST");
        DB_KEYWORDS.add("COLLATE");

        // query
        DB_KEYWORDS.add("AS");
        DB_KEYWORDS.add("LIKE");
        DB_KEYWORDS.add("IN");
        DB_KEYWORDS.add("BETWEEN");
        DB_KEYWORDS.add("AND");
        DB_KEYWORDS.add("OR");
        DB_KEYWORDS.add("NOT");
        DB_KEYWORDS.add("NULL");
        DB_KEYWORDS.add("IS");
        DB_KEYWORDS.add("CASE");
        DB_KEYWORDS.add("THEN");
        DB_KEYWORDS.add("ELSE");
        DB_KEYWORDS.add("END");
        DB_KEYWORDS.add("GROUP BY");
        DB_KEYWORDS.add("ORDER BY");
        DB_KEYWORDS.add("LIMIT");
        DB_KEYWORDS.add("HAVING");
        DB_KEYWORDS.add("ON");
        DB_KEYWORDS.add("JOIN");
        DB_KEYWORDS.add("LEFT JOIN");
        DB_KEYWORDS.add("RIGHT JOIN");
        DB_KEYWORDS.add("CROSS JOIN");
        DB_KEYWORDS.add("FULL JOIN");
        DB_KEYWORDS.add("INNER JOIN");
        DB_KEYWORDS.add("INTERSECT");
        DB_KEYWORDS.add("UNION");
        DB_KEYWORDS.add("UNION ALL");
        DB_KEYWORDS.add("EXCEPT");
        DB_KEYWORDS.add("COUNT");
        DB_KEYWORDS.add("SUM");
        DB_KEYWORDS.add("MAX");
        DB_KEYWORDS.add("MIN");
        DB_KEYWORDS.add("AVG");
        DB_KEYWORDS.add("DISTINCT");
        DB_KEYWORDS.add("ASC");
        DB_KEYWORDS.add("DESC");
        DB_KEYWORDS.add("EXISTS");
        DB_KEYWORDS.add("ANY");
        DB_KEYWORDS.add("ALL");

        // 函数
        DB_KEYWORDS.add("CONCAT");
        DB_KEYWORDS.add("LENGTH");
        DB_KEYWORDS.add("SUBSTRING");
        DB_KEYWORDS.add("UPPER");
        DB_KEYWORDS.add("LOWER");
        DB_KEYWORDS.add("TRIM");
        DB_KEYWORDS.add("ROUND");
    }

    public static List<String> getKeywords() {
        return DB_KEYWORDS;
    }

    /**
     * 初始化提示词
     *
     * @param token   提示词
     * @param minCorr 最低相关度
     * @return 结果
     */
    public static List<MysqlQueryPromptItem> initPrompts(MysqlQueryToken token, float minCorr) {
        if (token == null || token.isEmpty()) {
            return Collections.emptyList();
        }
        // 当前提示词
        String text = token.getContent().toUpperCase();
        // 提示词列表
        final List<MysqlQueryPromptItem> items = new CopyOnWriteArrayList<>();
        // 任务列表
        List<Runnable> tasks = new ArrayList<>();
        // 关键字
        if (token.isPossibilityKeyword()) {
            tasks.add(() -> MysqlQueryUtil.getKeywords().parallelStream().forEach(keyword -> {
                // 计算相关度
                double corr = ShellQueryUtil.clacCorr(keyword, text);
                if (corr > minCorr) {
                    MysqlQueryPromptItem item = new MysqlQueryPromptItem();
                    item.setType((byte) 4);
                    item.setContent(keyword);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 执行任务
        ThreadUtil.submit(tasks);
        // 根据相关度排序
        List<MysqlQueryPromptItem> itemList = items.parallelStream().sorted(Comparator.comparingDouble(MysqlQueryPromptItem::getCorrelation)).collect(Collectors.toList());
        // 反转列表
        return itemList.reversed();
    }
}
