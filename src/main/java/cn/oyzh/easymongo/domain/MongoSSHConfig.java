package cn.oyzh.easymongo.domain;

import cn.oyzh.ssh.domain.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * MongoDB连接的ssh配置
 *
 * @author oyzh
 * @since 2024-09-26
 */
@Table("t_ssh_config")
public class MongoSSHConfig extends SSHConnect implements Serializable {

    /**
     * 连接id
     * @see MongoConnect
     */
    @Column
    @PrimaryKey
    private String iid;

    /**
     * 获取连接id
     *
     * @return 连接id
     */
    public String getIid() {
        return iid;
    }

    /**
     * 设置连接id
     *
     * @param iid 连接id
     */
    public void setIid(String iid) {
        this.iid = iid;
    }
}
