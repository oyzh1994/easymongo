//package cn.oyzh.easymongo.test;
//
//import com.mongodb.client.*;
//import org.bson.Document;
//import org.graalvm.polyglot.*;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.*;
//
//public class MongoShellClient {
//
//    public static void main(String[] args) {
//        try (MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@120.24.176.61:27017/admin")) {
//            // 初始化 GraalVM JS 环境
//            Context context = Context.newBuilder("js")
//                    .allowAllAccess(true)  // 允许脚本访问 Java 类
//                    .build();
//
//            // 注入包装后的 db 对象
//            context.getBindings("js").putMember("db", new DatabaseWrapper(mongoClient));
//
//            // 注入辅助函数 use()
//            context.eval("js", "var use = function(dbName) { db = db.getSiblingDB(dbName); print('switched to db ' + dbName); };");
//
//            // REPL 循环
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                System.out.print("> ");
//                String line = reader.readLine();
//                if (line == null || line.equals("exit")) break;
//
//                // 预处理特殊命令
//                if (line.startsWith("show dbs")) {
//                    printDatabases(mongoClient);
//                    continue;
//                }
//                if (line.startsWith("show collections")) {
//                    // 需要知道当前 db，可以从 context 中取出 db 对象处理
//                    continue;
//                }
//                if (line.startsWith("use ")) {
//                    String dbName = line.substring(4).trim();
//                    context.eval("js", "use('" + dbName + "')");
//                    continue;
//                }
//
//                // 否则当作 JavaScript 执行
//                try {
//                    Value result = context.eval("js", line);
//                    if (result != null && !result.isNull() && !result.toString().isEmpty()) {
//                        System.out.println(result);
//                    }
//                } catch (Exception e) {
//                    System.err.println("Error: " + e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void printDatabases(MongoClient client) {
//        for (String name : client.listDatabaseNames()) {
//            System.out.println(name);
//        }
//    }
//
//    // ---------- 包装类 ----------
//    public static class DatabaseWrapper {
//        private final MongoClient client;
//        private String currentDbName = "test";
//
//        public DatabaseWrapper(MongoClient client) {
//            this.client = client;
//        }
//
//        public DatabaseWrapper getSiblingDB(String name) {
//            this.currentDbName = name;
//            return this;
//        }
//
//        public CollectionWrapper getCollection(String name) {
//            MongoDatabase db = client.getDatabase(currentDbName);
//            return new CollectionWrapper(db.getCollection(name));
//        }
//    }
//
//    public static class CollectionWrapper {
//        private final MongoCollection<Document> collection;
//
//        public CollectionWrapper(MongoCollection<Document> collection) {
//            this.collection = collection;
//        }
//
//        // 核心方法：find(query) 返回游标包装器
//        public CursorWrapper find(Object query) {
//            Document filter;
//            if (query instanceof Map) {
//                // GraalVM JS 对象会转为 Map
//                filter = new Document((Map<String, Object>) query);
//            } else {
//                filter = new Document(); // 无查询条件
//            }
//            FindIterable<Document> iter = collection.find(filter);
//            return new CursorWrapper(iter);
//        }
//
//        // 插入示例
//        public void insertOne(Object doc) {
//            if (doc instanceof Map) {
//                collection.insertOne(new Document((Map<String, Object>) doc));
//            }
//        }
//    }
//
//    public static class CursorWrapper {
//        private final FindIterable<Document> cursor;
//
//        public CursorWrapper(FindIterable<Document> cursor) {
//            this.cursor = cursor;
//        }
//
//        public CursorWrapper limit(int n) {
//            return new CursorWrapper(cursor.limit(n));
//        }
//
//        public CursorWrapper skip(int n) {
//            return new CursorWrapper(cursor.skip(n));
//        }
//
//        public List<Map<String, Object>> toArray() {
//            List<Map<String, Object>> list = new ArrayList<>();
//            for (Document doc : cursor) {
//                list.add(new LinkedHashMap<>(doc)); // 转为 Map 以便 JS 打印
//            }
//            return list;
//        }
//
//        // 模拟 pretty() - 返回格式化的 JSON 字符串
//        public String pretty() {
//            StringBuilder sb = new StringBuilder("[\n");
//            boolean first = true;
//            for (Document doc : cursor) {
//                if (!first) sb.append(",\n");
//                sb.append(doc.toJson());
//                first = false;
//            }
//            sb.append("\n]");
//            return sb.toString();
//        }
//
//        @Override
//        public String toString() {
//            // 默认调用 toArray() 并打印
//            return toArray().toString();
//        }
//    }
//}