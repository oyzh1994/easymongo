package cn.oyzh.easymongo.test;

import cn.oyzh.easymongo.shell.ShellMongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellTest {

    static void main() throws IOException {
        new ShellTest().test1();
    }

    private Context initJs() {
        MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@120.24.176.61:27017/admin");
        // 初始化 GraalVM JS 环境
        Context context = Context.newBuilder("js")
                .allowAllAccess(true)  // 允许脚本访问 Java 类
                .build();

        MongoDatabase database = mongoClient.getDatabase("test");
        // 注入包装后的 db 对象
        Value value = context.getBindings("js");
        value.putMember("db", new ShellMongoDatabase(database));
        return context;
    }

    private void printResult(Value result) {
        if (result != null && !result.isNull() && !result.toString().isEmpty()) {
            // 如果是游标包装器，自动转为格式化的 JSON 字符串输出
            if (result.canInvokeMember("pretty")) {
                System.out.println(result.invokeMember("pretty"));
            } else if (result.canInvokeMember("toArray")) {
                System.out.println(result.invokeMember("toString"));
            } else {
                System.out.println(result);
            }
        }
    }

    @Test
    public void test1() throws IOException {
        // 初始化 GraalVM JS 环境
        Context context = this.initJs();

        // REPL 循环
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null || line.equals("exit")) {
                break;
            }
            // 否则当作 JavaScript 执行
            try {
                Value result = context.eval("js", line);
                printResult(result);
            } catch (Exception e) {
                e.printStackTrace();
                //System.err.println("Error: " + e.getMessage());
                System.out.print("> ");
            }
        }
    }

    @Test
    public void getName() {
        // 初始化 GraalVM JS 环境
        Context context = initJs();

        String cmd = "db.getName()";
        Value result = context.eval("js", cmd);
        printResult(result);
    }

    @Test
    public void listCollectionNames() {
        // 初始化 GraalVM JS 环境
        Context context = initJs();

        String cmd = "db.listCollectionNames()";
        Value result = context.eval("js", cmd);
        printResult(result);
    }

    @Test
    public void listCollections() {
        // 初始化 GraalVM JS 环境
        Context context = initJs();

        String cmd = "db.listCollections()";
        Value result = context.eval("js", cmd);
        printResult(result);
    }
}


