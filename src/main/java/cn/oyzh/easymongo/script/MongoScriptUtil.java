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
 * MongoDB 脚本工具类，提供脚本对象到 BSON 文档的转换以及脚本可调用函数的收集能力
 *
 * @author oyzh
 * @since 2026-06-10
 */
public class MongoScriptUtil {

    /**
     * 获取数据库封装对外暴露的函数名称集合
     *
     * @return 函数名称集合
     */
    public static Set<String> databaseFuncions() {
        return functions(MongoScriptDatabase.class);
    }

    /**
     * 获取集合封装对外暴露的函数名称集合
     *
     * @return 函数名称集合
     */
    public static Set<String> collectionFuncions() {
        return functions(MongoScriptCollection.class);
    }

    /**
     * 收集指定类型对外暴露的函数名称集合，排除静态、原生、受保护、私有方法及 Object 基础方法
     *
     * @param clazz 目标类型
     * @return 函数名称集合
     */
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

    // === 公开 API ===

    /**
     * 将脚本数组或集合转换为 BSON 文档列表
     *
     * @param obj 待转换的脚本对象
     * @return BSON 文档列表
     */
    public static List<Document> toDocumentList(Object obj) {
        List<Document> list = new ArrayList<>();
        Collection<?> values;
        if (obj instanceof ScriptObjectMirror mirror) {
            values = mirror.values();
        } else if (obj instanceof Collection<?> c) {
            values = c;
        } else {
            return list;
        }
        for (Object o : values) {
            Document doc = toDocument(o);
            if (doc != null) {
                list.add(doc);
            }
        }
        return list;
    }

    /**
     * obj → Document。ScriptObjectMirror 数组返回 null。
     *
     * @param obj 待转换的脚本对象
     * @return 转换后的 BSON 文档，无法转换时返回 null
     */
    public static Document toDocument(Object obj) {
        if (obj instanceof ScriptObjectMirror mirror) {
            return mirror.isArray() ? null : toDocument(mirror);
        }
        if (obj instanceof Map map) {
            return toDocument((Map<String, Object>) map);
        }
        return null;
    }

    /**
     * obj → Document，非 Map 时返回空 Document（filter 默认值场景）
     *
     * @param obj 待转换的脚本对象
     * @return 转换后的 BSON 文档，无法转换时返回空文档
     */
    public static Document toDocumentOrDefault(Object obj) {
        Document doc = toDocument(obj);
        return doc != null ? doc : new Document();
    }

    /**
     * 递归转换：ScriptObjectMirror 数组→List，对象→Document，Collection→List
     *
     * @param value 待转换的值
     * @return 转换后的值
     */
    public static Object convertValue(Object value) {
        if (value instanceof ScriptObjectMirror mirror) {
            if (mirror.isArray()) {
                List<Object> list = new ArrayList<>();
                for (Object item : mirror.values()) {
                    list.add(convertValue(item));
                }
                return list;
            }
            return toDocument(mirror);
        }
        if (value instanceof Map map) {
            return toDocument(map);
        }
        if (value instanceof Collection collection) {
            List<Object> list = new ArrayList<>();
            for (Object item : collection) {
                list.add(convertValue(item));
            }
            return list;
        }
        return value;
    }

    /**
     * Map → Document，递归转换所有 value
     *
     * @param map 待转换的 Map
     * @return 转换后的 BSON 文档
     */
    public static Document toDocument(Map<String, Object> map) {
        Document doc = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            doc.put(entry.getKey(), convertValue(entry.getValue()));
        }
        return doc;
    }
}
