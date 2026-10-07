package cn.oyzh.easymongo.script;

import cn.oyzh.common.json.JSONUtil;
import com.mongodb.client.MongoIterable;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB 脚本游标的通用封装，负责将底层可迭代结果转换为脚本可用的形式
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoScriptCursor {

    /** 底层 MongoDB 可迭代结果 */
    private final MongoIterable<?> cursor;

    /**
     * 构造游标封装
     *
     * @param cursor 底层 MongoDB 可迭代结果
     */
    public MongoScriptCursor(MongoIterable<?> cursor) {
        this.cursor = cursor;
    }

    /**
     * 将游标结果格式化为易读的 JSON 字符串
     *
     * @return 格式化后的 JSON 字符串
     */
    public String pretty() {
        List list = new ArrayList<>();
        this.cursor.forEach(list::add);
        return JSONUtil.toPretty(list);
    }

    /**
     * 将游标结果收集为列表
     *
     * @return 游标结果列表
     */
    public List<?> toArray() {
//        if (this.cursor.first() instanceof Document) {
//            List<Map<String, Object>> list = new ArrayList<>();
//            for (Object doc : this.cursor) {
//                Document d = (Document) doc;
//                list.add(new LinkedHashMap<>(d));
//            }
//            return list;
//        }
//
//        if (this.cursor.first() instanceof String) {
//            List list = new ArrayList<>();
//            this.cursor.forEach(list::add);
//            return list;
//        }
//        return Collections.emptyList();
        List list = new ArrayList<>();
        this.cursor.forEach(list::add);
        return list;

    }

    @Override
    public String toString() {
        return this.pretty();
    }
}