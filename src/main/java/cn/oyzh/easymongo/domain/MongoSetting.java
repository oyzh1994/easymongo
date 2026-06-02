package cn.oyzh.easymongo.domain;


import cn.oyzh.fx.plus.domain.AppSetting;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;

/**
 * db设置
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Table("t_setting")
public class MongoSetting extends AppSetting {

    /**
     * 记录每页限制
     */
    @Column
    private Integer recordPageLimit;

    public void setRecordPageLimit(Integer recordPageLimit) {
        if (recordPageLimit == null || recordPageLimit <= 0) {
            this.recordPageLimit = 100;
        } else {
            this.recordPageLimit = recordPageLimit;
        }
    }

    public int getRecordPageLimit() {
        if (this.recordPageLimit == null || this.recordPageLimit <= 0) {
            return 100;
        }
        return this.recordPageLimit;
    }
}
