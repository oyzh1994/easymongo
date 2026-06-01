package cn.oyzh.easymongo.trees;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.trees.connect.MongoConnectTreeItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接管理
 *
 * @author oyzh
 * @since 2023/5/12
 */
public interface MongoConnectManager {

    /**
     * 添加连接
     *
     * @param redisInfo 连接信息
     */
    void addConnect( MongoConnect redisInfo);

    /**
     * 删除多个连接
     *
     * @param redisInfos 连接列表
     */
    default void addConnects(List<MongoConnect> redisInfos) {
        if (CollectionUtil.isNotEmpty(redisInfos)) {
            for (MongoConnect redisInfo : redisInfos) {
                this.addConnect(redisInfo);
            }
        }
    }

    /**
     * 添加连接键
     *
     * @param item 连接键
     */
    void addConnectItem( MongoConnectTreeItem item);

    /**
     * 添加多个连接键
     *
     * @param items 连接键列表
     */
    void addConnectItems( List<MongoConnectTreeItem> items);

    /**
     * 删除连接键
     *
     * @param item 连接键
     * @return 结果
     */
    boolean delConnectItem( MongoConnectTreeItem item);

    /**
     * 获取连接键
     *
     * @return 连接键
     */
    List<MongoConnectTreeItem> getConnectItems();

    /**
     * 获取已连接的连接节点
     *
     * @return 已连接的连接节点
     */
    default List<MongoConnectTreeItem> getConnectedItems() {
        return this.getConnectItems().parallelStream().filter(MongoConnectTreeItem::isConnected).collect(Collectors.toList());
    }

}
