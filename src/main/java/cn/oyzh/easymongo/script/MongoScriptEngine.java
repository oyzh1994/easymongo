package cn.oyzh.easymongo.script;

import cn.oyzh.easymongo.script.function.MongoScriptBinaryFcuntion;
import cn.oyzh.easymongo.script.function.MongoScriptCodeFunction;
import cn.oyzh.easymongo.script.function.MongoScriptISODateFunction;
import cn.oyzh.easymongo.script.function.MongoScriptInit32Function;
import cn.oyzh.easymongo.script.function.MongoScriptLongFunction;
import cn.oyzh.easymongo.script.function.MongoScriptObjectIdFunction;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * MongoDB 脚本引擎封装，基于 Nashorn 构建并注入 MongoDB 相关全局对象与函数
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoScriptEngine {

    /** MongoDB 客户端 */
    private final MongoClient mongoClient;

    /**
     * 构造脚本引擎并完成初始化
     *
     * @param mongoClient MongoDB 客户端
     */
    public MongoScriptEngine(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        this.initEngine();
    }

    /** 脚本引擎实例 */
    private javax.script.ScriptEngine engine;

    /** 脚本引擎作用域绑定 */
    private Bindings bindings;

    /**
     * 初始化脚本引擎，注入 MongoDB 特殊类型构造函数
     */
    private void initEngine() {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        this.engine = factory.getScriptEngine("--language=es6", "-scripting");
        this.bindings = this.engine.getBindings(ScriptContext.ENGINE_SCOPE);

        // 注入 MongoDB 特殊类型构造函数
        this.engine.put("Code", new MongoScriptCodeFunction());
        this.engine.put("Long", new MongoScriptLongFunction());
        this.engine.put("Int32", new MongoScriptInit32Function());
        this.engine.put("Binary", new MongoScriptBinaryFcuntion());
        this.engine.put("ISODate", new MongoScriptISODateFunction());
        this.engine.put("ObjectId", new MongoScriptObjectIdFunction());
    }

    /**
     * 获取脚本引擎实例
     *
     * @return 脚本引擎实例
     */
    public ScriptEngine getEngine() {
        return engine;
    }

    /**
     * 切换当前脚本操作的数据库
     *
     * @param dbName 数据库名称
     */
    public void db(String dbName) {
        // 注入包装后的 db 对象
        MongoDatabase database = this.mongoClient.getDatabase(dbName);
        this.bindings.put("db", new MongoScriptDatabase(database));
        this.engine.put("dbName", dbName);
    }

    /**
     * 执行脚本
     *
     * @param script 脚本内容
     * @return 脚本执行结果
     * @throws ScriptException 脚本执行失败时抛出
     */
    public Object eval(String script) throws ScriptException {
        return this.engine.eval(script);
    }

    /**
     * 向脚本引擎注入变量
     *
     * @param key 变量名称
     * @param val 变量值
     */
    public void put(String key, Object val) {
        this.engine.put(key, val);
    }
}
