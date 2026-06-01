package cn.oyzh.easymongo.domain;

import cn.oyzh.ssh.domain.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
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

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }
}
