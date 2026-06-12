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
public class MongoQueryUtil {

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
                    // 更新集合索引
                    List<MongoCollection> collections = client.listCollections(dbName);
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
}
