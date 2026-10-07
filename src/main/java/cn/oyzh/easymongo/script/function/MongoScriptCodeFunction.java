package cn.oyzh.easymongo.script.function;

import org.bson.types.Code;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.internal.runtime.Undefined;

import java.util.Date;

/**
 * 脚本环境中的 Code 构造函数，用于将脚本内容封装为 BSON 代码类型
 *
 * @author oyzh
 * @since 2026-06-17
 */
public class MongoScriptCodeFunction extends AbstractJSObject {

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
            return new Date();
        }
        return new Code(args[0].toString());
    }
}