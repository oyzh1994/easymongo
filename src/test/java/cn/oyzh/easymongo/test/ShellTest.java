package cn.oyzh.easymongo.test;

import cn.oyzh.easymongo.script.MongoScriptCursor;
import cn.oyzh.easymongo.script.MongoScriptDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.Test;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellTest {

    static void main() throws IOException {
        new ShellTest().test1();
    }

    private ScriptEngine initJs() {
        MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@127.0.0.1:27017/admin");

        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine( );

        MongoDatabase database = mongoClient.getDatabase("test");
        // 注入包装后的 db 对象
        Bindings value = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        value.put("db", new MongoScriptDatabase(database));
        return engine;
    }

    private void printResult(Object result) {
        if (result != null) {
            // 如果是游标包装器，自动转为格式化的 JSON 字符串输出
            if (result instanceof MongoScriptCursor cursor) {
                System.out.println(cursor.pretty());
            } else {
                System.out.println(result);
            }
        }
    }

    @Test
    public void test1() throws IOException {
        // 初始化 GraalVM JS 环境
        ScriptEngine context = this.initJs();

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
                Object result = context.eval(line);
                printResult(result);
            } catch (Exception e) {
                e.printStackTrace();
                //System.err.println("Error: " + e.getMessage());
                System.out.print("> ");
            }
        }
    }

    @Test
    public void getName() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();

        String cmd = "db.getName()";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void listCollectionNames() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();

        String cmd = "db.listCollectionNames()";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void listCollections() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();

        String cmd = "db.listCollections()";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void find() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();
        //String cmd = "db.getCollection('test').find()";
        String cmd = "db.test.find()";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void find_1() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();
        //String cmd = "db.getCollection('test').find({'a':1})";
        String cmd = "db.test.find({'a':1})";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void find_2() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();
        //String cmd = "db.getCollection('test').find({'a':1})";
        String cmd = "db.getCollection('test').find().explain()";
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void insert() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();
        //String cmd = """
        //        db.getCollection("test").insert({
        //            a: 1
        //        });
        //        """;
        String cmd = """
                db.test.insert({
                    a: 1
                });
                """;
        Object result = engine.eval(cmd);
        printResult(result);
    }

    @Test
    public void delete() throws ScriptException {
        // 初始化 GraalVM JS 环境
        ScriptEngine engine = initJs();
        String cmd = """
                db.test.delete({
                    a: 1
                });
                """;
        Object result = engine.eval(cmd);
        printResult(result);
    }
}


