package cn.oyzh.easymongo.shell;

import cn.oyzh.common.util.Base64Util;

/**
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class ShellBinary {

    public org.bson.types.Binary createFromBase64(String base64, int type) {
        byte[] data = Base64Util.decode(base64);
        return new org.bson.types.Binary((byte) type, data);
    }
}
