package cn.oyzh.easymongo.script.function;

import cn.oyzh.easymongo.util.MongoUtil;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;
import org.openjdk.nashorn.internal.runtime.Undefined;

import java.util.Date;

/**
 * 脚本环境中的 ISODate 构造函数，用于将日期字符串解析为日期对象
 *
 * @author oyzh
 * @since 2026-06-17
 */
public class MongoScriptISODateFunction extends AbstractJSObject {

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
        if (!args[0].toString().isEmpty()) {
            try {
                return MongoUtil.DATE_FORMAT.parse(args[0].toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new Date();
    }
}