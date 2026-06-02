package cn.oyzh.easymongo.mongo.condition;

import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoRecordFilter;
import cn.oyzh.easymongo.util.MongoNodeUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import com.mongodb.client.model.Filters;
import javafx.scene.Node;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 条件工具类
 *
 * @author oyzh
 * @since 2024/6/26
 */
public class MysqlConditionUtil {

    /**
     * 获取条件
     *
     * @return 条件列表
     */
    public static List<MysqlCondition> conditions() {
        List<MysqlCondition> conditions = new ArrayList<>();
        conditions.add(MysqlContainsCondition.INSTANCE);
        conditions.add(MysqlNotContainsCondition.INSTANCE);
        conditions.add(MysqlEqCondition.INSTANCE);
        conditions.add(MysqlNotEqCondition.INSTANCE);
        conditions.add(MysqlGtCondition.INSTANCE);
        conditions.add(MysqlGtEqCondition.INSTANCE);
        conditions.add(MysqlLtEqCondition.INSTANCE);
        conditions.add(MysqlLtCondition.INSTANCE);
        conditions.add(MysqlNullCondition.INSTANCE);
        conditions.add(MysqlNotNullCondition.INSTANCE);
        conditions.add(MysqlEmptyCondition.INSTANCE);
        conditions.add(MysqlNotEmptyCondition.INSTANCE);
        conditions.add(MysqlInListCondition.INSTANCE);
        conditions.add(MysqlNotInListCondition.INSTANCE);
        conditions.add(MysqlBetweenCondition.INSTANCE);
        conditions.add(MysqlNotBetweenCondition.INSTANCE);
        conditions.add(MysqlStartWithCondition.INSTANCE);
        conditions.add(MysqlNotStartWithCondition.INSTANCE);
        conditions.add(MysqlEndWithCondition.INSTANCE);
        conditions.add(MysqlNotEndWithCondition.INSTANCE);
        return conditions;
    }

    /**
     * 构建条件
     *
     * @param filters 过滤条件
     * @return 条件
     */
    public static Bson buildCondition(List<MongoRecordFilter> filters) {
        Bson bson = new Document();
        if (filters == null || filters.isEmpty()) {
            return bson;
        }
        for (MongoRecordFilter filter : filters) {
            if (!filter.isEnabled()) {
                continue;
            }
            Bson bson1 = filter.condition();
            if (bson1 != null) {
                if ("and".equalsIgnoreCase(filter.getJoinSymbol())) {
                    bson = Filters.and(bson1);
                } else {
                    bson = Filters.or(bson1);
                }
            }
        }
        return bson;
    }

    /**
     * 是否in条件
     *
     * @param condition 条件
     * @return 结果
     */
    public static boolean isInCondition(MysqlCondition condition) {
        return condition == MysqlInListCondition.INSTANCE || condition == MysqlNotInListCondition.INSTANCE;
    }

    /**
     * 是否介于条件
     *
     * @param condition 条件
     * @return 结果
     */
    public static boolean isBetweenCondition(MysqlCondition condition) {
        return condition == MysqlBetweenCondition.INSTANCE || condition == MysqlNotBetweenCondition.INSTANCE;
    }

    /**
     * 生成节点
     *
     * @param column    字段
     * @param condition 条件
     * @return 节点
     */
    public static List<Node> generateNode(MongoColumn column, MysqlCondition condition) {
        condition = condition == null ? conditions().getFirst() : condition;
        List<Node> list = new ArrayList<>();
        if (isInCondition(condition)) {
            ClearableTextField node = new ClearableTextField();
            node.setDisable(!condition.isRequireCondition());
            list.add(node);
        } else if (isBetweenCondition(condition)) {
            Node node1 = MongoNodeUtil.generateNode(column);
            Node node2 = MongoNodeUtil.generateNode(column);
            node1.setDisable(!condition.isRequireCondition());
            node2.setDisable(!condition.isRequireCondition());
            list.add(node1);
            list.add(node2);
        } else {
            Node node = MongoNodeUtil.generateNode(column);
            node.setDisable(!condition.isRequireCondition());
            list.add(node);
        }
        return list;
    }

    /**
     * 设置节点值
     *
     * @param controls 组件
     * @param value    值
     */
    public static void setNodeVal(List<Node> controls, Object value) {
        for (int i = 0; i < controls.size(); i++) {
            if (value instanceof List<?> list) {
                MongoNodeUtil.setNodeVal(controls.get(i), list.get(i));
            } else {
                MongoNodeUtil.setNodeVal(controls.get(i), value);
            }
        }
    }

    /**
     * 获取节点值
     *
     * @param controls 组件
     * @return 值
     */
    public static Object getNodeVal(List<Node> controls) {
        if (controls == null || controls.isEmpty()) {
            return null;
        }
        if (controls.size() == 1) {
            return MongoNodeUtil.getNodeVal(controls.getFirst());
        }
        List<Object> list = new ArrayList<>();
        for (Node control : controls) {
            list.add(MongoNodeUtil.getNodeVal(control));
        }
        return list;
    }

    public static Bson idFilterRegex(Pattern pattern) {
        return Filters.expr(
                new Document("$regexMatch",
                        new Document("input", new Document("$toString", "$_id"))
                                .append("regex", pattern.pattern())
                )
        );
    }

    public static Bson idFilterRegexNot(Pattern pattern) {
        return Filters.expr(
                new Document("$not",
                        new Document("$regexMatch",
                                new Document("input", new Document("$toString", "$_id"))
                                        .append("regex", pattern.pattern())
                        )
                )
        );
    }
}
