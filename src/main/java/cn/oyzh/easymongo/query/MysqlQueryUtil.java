package cn.oyzh.easymongo.query;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.easymongo.mongo.MongoClient;
import cn.oyzh.easymongo.mongo.MongoCollection;
import cn.oyzh.easymongo.script.MongoScriptUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private static final Set<String> DB_KEYWORDS = new HashSet<>();

    /**
     * 函数
     */
    private static final Set<String> DB_FUNCTIONS = new HashSet<>();

    /**
     * 集合
     */
    private static final List<MongoCollection> DB_COLLECTIONS = new ArrayList<>();

    static {
        DB_KEYWORDS.add("db");
        DB_FUNCTIONS.addAll(MongoScriptUtil.databasefuncions());
        DB_FUNCTIONS.addAll(MongoScriptUtil.collectionfuncions());
    }

    public static Set<String> getKeywords() {
        return DB_KEYWORDS;
    }

    public static Set<String> getFunctions() {
        return DB_FUNCTIONS;
    }

    public static List<MongoCollection> getCollections() {
        return DB_COLLECTIONS;
    }

    public static void updateIndex(MongoClient client, String dbName) {
        Runnable task = () -> {
            if (indexStatus == 0) {
                try {
                    indexStatus = 1;
                    DB_COLLECTIONS.clear();
                    // 更新库索引
                    List<MongoCollection> collections = client.selectCollections(dbName);
                    DB_COLLECTIONS.addAll(collections);
                    indexStatus = 2;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    indexStatus = 0;
                }
            }
        };
        ThreadUtil.start(task);
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
                double corr = TextUtil.clacCorr(keyword, text);
                if (corr > minCorr) {
                    MysqlQueryPromptItem item = new MysqlQueryPromptItem();
                    item.setType((byte) 4);
                    item.setContent(keyword);
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 集合
        if (token.isPossibilityCollection()) {
            tasks.add(() -> MysqlQueryUtil.getCollections().parallelStream().forEach(collection -> {
                // 计算相关度
                double corr = TextUtil.clacCorr(collection.getName(), text);
                if (corr > minCorr) {
                    MysqlQueryPromptItem item = new MysqlQueryPromptItem();
                    item.setType((byte) 1);
                    item.setContent(collection.getName());
                    item.setCorrelation(corr);
                    items.add(item);
                }
            }));
        }
        // 函数
        if (token.isPossibilityFunction()) {
            tasks.add(() -> MysqlQueryUtil.getFunctions().parallelStream().forEach(member -> {
                // 计算相关度
                double corr = TextUtil.clacCorr(member, text);
                if (corr > minCorr) {
                    MysqlQueryPromptItem item = new MysqlQueryPromptItem();
                    item.setType((byte) 2);
                    item.setContent(member + "()");
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
