package cn.oyzh.easymongo.shell;

import cn.oyzh.easymongo.util.MongoUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Date;

/**
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class ShellEngine {

    private final MongoClient mongoClient;

    public ShellEngine(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.initEngine();
    }

    private ScriptEngine engine;

    private Bindings bindings;

    private void initEngine() {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        this.engine = factory.getScriptEngine("--language=es6", "-scripting");
        this.bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);

        // 注入 MongoDB 特殊类型构造函数
        this.engine.put("Binary", new ShellBinary());
        this.engine.put("ObjectId", (java.util.function.Function<String, ObjectId>) ObjectId::new);
        this.engine.put("ISODate", (java.util.function.Function<String, Date>) dateStr -> {
            try {
                return MongoUtil.DATE_FORMAT.parse(dateStr);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        });
        this.engine.put("Int32", (java.util.function.Function<Object, Integer>) val -> {
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            return Integer.parseInt(val.toString());
        });
        this.engine.put("Long", (java.util.function.Function<Object, Long>) val -> {
            if (val instanceof Number) {
                return ((Number) val).longValue();
            }
            return Long.parseLong(val.toString());
        });
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public void db(String dbName) {
        // 注入包装后的 db 对象
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        this.bindings.put("db", new ShellMongoDatabase(database));
        this.engine.put("dbName", dbName);
    }

    public Object eval(String script) throws ScriptException {
        return this.engine.eval(script);
    }

    public void put(String key, Object val) {
        this.engine.put(key, val);
    }
}
