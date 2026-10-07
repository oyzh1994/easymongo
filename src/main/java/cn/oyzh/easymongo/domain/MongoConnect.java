package cn.oyzh.easymongo.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.BooleanUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB连接信息
 *
 * @author oyzh
 * @since 2020/3/6
 */
@Table("t_connect")
public class MongoConnect implements Serializable, Comparable<MongoConnect>, ObjectComparator<MongoConnect> {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String id;

    /**
     * 连接地址
     */
    @Column
    private String host;

    /**
     * 名称
     */
    @Column
    private String name;

    /**
     * 认证用户
     */
    @Column
    private String user;

    /**
     * 类型
     */
    @Column
    private String type;

    /**
     * 认证方式
     */
    @Column
    private String authType;

    /**
     * 认证数据库
     */
    @Column
    private String authDatabase;

    /**
     * 认证密码
     */
    @Column
    private String password;

    /**
     * 备注信息
     */
    @Column
    private String remark;

    /**
     * 只读模式
     */
    @Column
    private Boolean readonly;

    /**
     * 分组id
     */
    @Column
    private String groupId;

    /**
     * 收藏列表
     */
    private List<String> collects;

    /**
     * 连接超时时间
     */
    @Column
    private Integer connectTimeOut;

    /**
     * 是否开启ssh转发
     */
    @Column
    private Boolean sshForward;

    /**
     * ssh信息
     */
    private MongoSSHConfig sshConfig;

    /**
     * 服务标识
     */
    private String sid;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 复制对象
     *
     * @param info 连接信息
     * @return 当前对象
     */
    public MongoConnect copy(MongoConnect info) {
        this.name = info.name;
        this.host = info.host;
        this.user = info.user;
        this.type = info.type;
        this.remark = info.remark;
        this.groupId = info.groupId;
        this.readonly = info.readonly;
        this.authType = info.authType;
        this.authDatabase = info.authDatabase;
        this.password = info.password;
        this.collects = info.collects;
        this.sshConfig = info.sshConfig;
        this.sshForward = info.sshForward;
        this.connectTimeOut = info.connectTimeOut;
        return this;
    }

    /**
     * 是否ssh转发
     *
     * @return 结果
     */
    public boolean isSSHForward() {
        return BooleanUtil.isTrue(this.sshForward);
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return BooleanUtil.isTrue(this.readonly);
    }

    /**
     * 是否被收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean isCollect( String path) {
        return CollectionUtil.isNotEmpty(this.collects) && this.collects.contains(path);
    }

    /**
     * 添加收藏
     *
     * @param path 路径
     */
    public void addCollect( String path) {
        if (this.collects == null) {
            this.collects = new ArrayList<>();
        }
        if (!this.collects.contains(path)) {
            this.collects.add(path);
        }
    }

    /**
     * 取消收藏
     *
     * @param path 路径
     * @return 结果
     */
    public boolean removeCollect( String path) {
        if (this.collects != null) {
            return this.collects.remove(path);
        }
        return false;
    }

    /**
     * 获取连接超时
     *
     * @return 连接超时时间
     */
    public Integer getConnectTimeOut() {
        return this.connectTimeOut == null || this.connectTimeOut < 1 ? 5 : this.connectTimeOut;
    }

    /**
     * 获取连接超时毫秒值
     *
     * @return 连接超时时间毫秒值
     */
    public int connectTimeOutMs() {
        return this.getConnectTimeOut() * 1000;
    }

    @Override
    public int compareTo(MongoConnect o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(o.getName());
    }

    /**
     * 获取连接ip
     *
     * @return 连接ip
     */
    public String hostIp() {
        if (StringUtil.isNotBlank(this.host) && this.host.contains(":")) {
            return this.host.split(":")[0];
        }
        return "";
    }

    /**
     * 获取连接端口
     *
     * @return 连接端口
     */
    public int hostPort() {
        try {
            if (StringUtil.isNotBlank(this.host) && !this.host.contains(",") && this.host.contains(":")) {
                return Integer.parseInt(this.host.split(":")[1]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean compare(MongoConnect t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.name, t1.name);
    }

    /**
     * 获取服务名称，优先返回服务标识
     *
     * @return 服务名称
     */
    public String serviceName() {
        return this.sid == null ? this.serviceName : this.sid;
    }

    /**
     * 检查使用的服务类型
     *
     * @return 服务类型，sid或serviceName
     */
    public String checkServiceType() {
        return this.sid == null ? "sid" : "serviceName";
    }

    /**
     * 获取数据id
     *
     * @return 数据id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置数据id
     *
     * @param id 数据id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置连接地址
     *
     * @param host 连接地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取认证用户
     *
     * @return 认证用户
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置认证用户
     *
     * @param user 认证用户
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 获取类型
     *
     * @return 类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置类型
     *
     * @param type 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取认证密码
     *
     * @return 认证密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置认证密码
     *
     * @param password 认证密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取备注信息
     *
     * @return 备注信息
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息
     *
     * @param remark 备注信息
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取只读模式
     *
     * @return 只读模式
     */
    public Boolean getReadonly() {
        return readonly;
    }

    /**
     * 设置只读模式
     *
     * @param readonly 只读模式
     */
    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 获取分组id
     *
     * @return 分组id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * 设置分组id
     *
     * @param groupId 分组id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 获取收藏列表
     *
     * @return 收藏列表
     */
    public List<String> getCollects() {
        return collects;
    }

    /**
     * 设置收藏列表
     *
     * @param collects 收藏列表
     */
    public void setCollects(List<String> collects) {
        this.collects = collects;
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeOut 连接超时时间
     */
    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    /**
     * 获取是否开启ssh转发
     *
     * @return 是否开启ssh转发
     */
    public Boolean getSshForward() {
        return sshForward;
    }

    /**
     * 设置是否开启ssh转发
     *
     * @param sshForward 是否开启ssh转发
     */
    public void setSshForward(Boolean sshForward) {
        this.sshForward = sshForward;
    }

    /**
     * 获取ssh信息
     *
     * @return ssh信息
     */
    public MongoSSHConfig getSshConfig() {
        return sshConfig;
    }

    /**
     * 设置ssh信息
     *
     * @param sshConfig ssh信息
     */
    public void setSshConfig(MongoSSHConfig sshConfig) {
        this.sshConfig = sshConfig;
    }

    /**
     * 获取服务标识
     *
     * @return 服务标识
     */
    public String getSid() {
        return sid;
    }

    /**
     * 设置服务标识
     *
     * @param sid 服务标识
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * 获取服务名称
     *
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置服务名称
     *
     * @param serviceName 服务名称
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 获取认证方式
     *
     * @return 认证方式
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * 设置认证方式
     *
     * @param authType 认证方式
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * 获取认证数据库
     *
     * @return 认证数据库
     */
    public String getAuthDatabase() {
        return authDatabase;
    }

    /**
     * 设置认证数据库
     *
     * @param authDatabase 认证数据库
     */
    public void setAuthDatabase(String authDatabase) {
        this.authDatabase = authDatabase;
    }

    /**
     * 是否为密码认证
     *
     * @return 结果
     */
    public boolean isPasswordAuth() {
        return "password".equalsIgnoreCase(this.authType);
    }
}
