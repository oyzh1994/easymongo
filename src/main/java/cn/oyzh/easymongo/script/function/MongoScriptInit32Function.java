package cn.oyzh.easymongo.script.function;

import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.internal.runtime.Undefined;

/**
 * 脚本环境中的 Int32 构造函数，用于将参数转换为 32 位整型
 *
 * @author oyzh
 * @since 2026-06-17
 */
public class MongoScriptInit32Function extends AbstractJSObject {

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
            return 0;
        }
        if (args[0] instanceof Number) {
            return ((Number) args[0]).intValue();
        }
        return Integer.parseInt(args[0].toString());
    }
}