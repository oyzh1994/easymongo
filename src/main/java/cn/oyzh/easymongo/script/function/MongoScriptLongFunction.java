package cn.oyzh.easymongo.script.function;

import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.internal.runtime.Undefined;

/**
 * 脚本环境中的 Long 构造函数，用于将参数转换为 64 位长整型
 *
 * @author oyzh
 * @since 2026-06-17
 */
public class MongoScriptLongFunction extends AbstractJSObject {

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object obj, Object... args) {
        if (args == null
                || args.length == 0
                || args[0] == null
                || args[0] instanceof Undefined) {
            return 0L;
        }
        if (args[0] instanceof Number) {
            return ((Number) args[0]).longValue();
        }
        return Long.parseLong(args[0].toString());
    }
}