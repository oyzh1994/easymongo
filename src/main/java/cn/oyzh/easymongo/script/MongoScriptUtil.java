package cn.oyzh.easymongo.script;

import cn.oyzh.common.util.ReflectUtil;
import org.bson.Document;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author oyzh
 * @since 2026-06-10
 */
public class MongoScriptUtil {

    public static Set<String> databasefuncions() {
        return functions(MongoScriptDatabase.class);
    }

    public static Set<String> collectionfuncions() {
        return functions(MongoScriptCollection.class);
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

    public static List<Document> toDocumentList(Object obj) {
        List<Document> list = new ArrayList<>();
        if (obj instanceof ScriptObjectMirror mirror) {
            for (Object o : mirror.values()) {
                if (o instanceof Map map) {
                    list.add(new Document(map));
                }
            }
        } else if (obj instanceof Collection collection) {
            for (Object o : collection) {
                if (o instanceof Map map) {
                    list.add(new Document(map));
                }
            }
        }
        return list;
    }
}
