package cn.oyzh.easymongo.mongo;


import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据库对象状态列表，维护对象的新增、变更、删除、正常等状态
 *
 * @author oyzh
 * @since 2024/07/13
 */
public abstract class DBObjectList<S extends DBObjectStatus> extends ArrayList<S> {

    /**
     * 类型：正常
     */
    public static final byte TYPE_NORMAL = 0;

    /**
     * 类型：已删除
     */
    public static final byte TYPE_DELETED = 1;

    /**
     * 类型：已新增
     */
    public static final byte TYPE_CREATED = 2;

    /**
     * 类型：已变更
     */
    public static final byte TYPE_CHANGED = 3;

    // protected void valueList(List<S> list) {
    //     if (CollUtil.isNotEmpty(list)) {
    //         this.clear();
    //         this.addAll(list);
    //     }
    // }

    /**
     * 是否存在状态非空的对象
     *
     * @return 是否存在
     */
    public boolean isChanged() {
        for (S s : this) {
            if (StringUtil.isNotBlank(s.getStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取已新增对象列表
     *
     * @return 已新增对象列表
     */
    public List<S> createdList() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        return this.stream().filter(DBObjectList::isCreated).collect(Collectors.toList());
    }

    /**
     * 获取已变更对象列表
     *
     * @return 已变更对象列表
     */
    public List<S> changedList() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        return this.stream().filter(DBObjectList::isChanged).collect(Collectors.toList());
    }

    /**
     * 获取已删除对象列表
     *
     * @return 已删除对象列表
     */
    public List<S> deletedList() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        return this.stream().filter(DBObjectList::isDeleted).collect(Collectors.toList());
    }

    /**
     * 获取正常状态对象列表
     *
     * @return 正常状态对象列表
     */
    public List<S> normalList() {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        return this.stream().filter(DBObjectList::isNormal).collect(Collectors.toList());
    }

    /**
     * 按类型筛选对象列表
     *
     * @param types 类型集合
     * @return 筛选结果
     */
    public List<S> filterList(byte... types) {
        if (this.isEmpty()) {
            return Collections.emptyList();
        }
        if (types == null || types.length == 0) {
            return this;
        }
        List<S> list = new ArrayList<>();
        for (byte type : types) {
            List<S> list1 = null;
            if (type == TYPE_NORMAL) {
                list1 = this.normalList();
            } else if (type == TYPE_CHANGED) {
                list1 = this.changedList();
            } else if (type == TYPE_CREATED) {
                list1 = this.createdList();
            } else if (type == TYPE_DELETED) {
                list1 = this.deletedList();
            }
            if (CollectionUtil.isNotEmpty(list1)) {
                list.addAll(list1);
            }
        }
        return list;
    }

    @Override
    public boolean add(S s) {
        if (s != null) {
            return super.add(s);
        }
        return false;
    }

    /**
     * 移除对象
     *
     * @param s 对象
     */
    public void remove(S s) {
        super.remove(s);
    }

    /**
     * 是否包含对象
     *
     * @param s 对象
     * @return 是否包含
     */
    public boolean contains(S s) {
        return super.contains(s);
    }

    /**
     * 是否存在已删除对象
     *
     * @return 是否存在
     */
    public boolean hasDeleted() {
        if (this.isEmpty()) {
            return false;
        }
        for (S s : this) {
            if (isDeleted(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在已新增对象
     *
     * @return 是否存在
     */
    public boolean hasCreated() {
        if (!this.isEmpty()) {
            for (S s : this) {
                if (isCreated(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否存在已变更对象
     *
     * @return 是否存在
     */
    public boolean hasChanged() {
        if (!this.isEmpty()) {
            for (S s : this) {
                if (isChanged(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否存在正常状态对象
     *
     * @return 是否存在
     */
    public boolean hasNormal() {
        if (!this.isEmpty()) {
            for (S s : this) {
                if (isNormal(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断对象是否已删除
     *
     * @param status 对象状态
     * @return 是否已删除
     */
    public static boolean isDeleted(DBObjectStatus status) {
        return status != null && status.isDeleted();
    }

    /**
     * 判断对象是否已新增
     *
     * @param status 对象状态
     * @return 是否已新增
     */
    public static boolean isCreated(DBObjectStatus status) {
        return status != null && !status.isDeleted() && status.isCreated();
    }

    /**
     * 判断对象是否已变更
     *
     * @param status 对象状态
     * @return 是否已变更
     */
    public static boolean isChanged(DBObjectStatus status) {
        return status != null && !status.isCreated() && !status.isDeleted() && status.isChanged();
    }

    /**
     * 判断对象是否正常
     *
     * @param status 对象状态
     * @return 是否正常
     */
    public static boolean isNormal(DBObjectStatus status) {
        if (status == null) {
            return false;
        }
        return !status.isChanged() && !status.isDeleted() && !status.isCreated();
    }
}

