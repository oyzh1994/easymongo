package cn.oyzh.easymongo.shell;

import cn.oyzh.common.util.ReflectUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author oyzh
 * @since 2026-06-10
 */
public class ShellUtil {

    public static Set<String> databasefuncions() {
        return functions(ShellMongoDatabase.class);
    }

    public static Set<String> collectionfuncions() {
        return functions(ShellMongoCollection.class);
    }

    public static Set<String> functions(Class<?> clazz) {
        Set<String> functions = new HashSet<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())
                    || Modifier.isNative(method.getModifiers())
                    || Modifier.isProtected(method.getModifiers())
                    || Modifier.isPrivate(method.getModifiers())
            ) {
                continue;
            }
            String mName = method.getName();
            if (ReflectUtil.objectMethodNames().contains(mName)) {
                continue;
            }
            functions.add(mName);
        }
        return functions;
    }
}
