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
 * db信息
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

    private String sid;

    private String serviceName;

    /**
     * 复制对象
     *
     * @param info db信息
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

    public String serviceName() {
        return this.sid == null ? this.serviceName : this.sid;
    }

    public String checkServiceType() {
        return this.sid == null ? "sid" : "serviceName";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getCollects() {
        return collects;
    }

    public void setCollects(List<String> collects) {
        this.collects = collects;
    }

    public void setConnectTimeOut(Integer connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public Boolean getSshForward() {
        return sshForward;
    }

    public void setSshForward(Boolean sshForward) {
        this.sshForward = sshForward;
    }

    public MongoSSHConfig getSshConfig() {
        return sshConfig;
    }

    public void setSshConfig(MongoSSHConfig sshConfig) {
        this.sshConfig = sshConfig;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthDatabase() {
        return authDatabase;
    }

    public void setAuthDatabase(String authDatabase) {
        this.authDatabase = authDatabase;
    }

    public boolean isPasswordAuth() {
        return "password".equalsIgnoreCase(this.authType);
    }
}
