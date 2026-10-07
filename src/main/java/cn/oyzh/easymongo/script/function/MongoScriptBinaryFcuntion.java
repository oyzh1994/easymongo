package cn.oyzh.easymongo.script.function;

import cn.oyzh.common.util.Base64Util;
import org.bson.types.Binary;

/**
 * 脚本环境中的 Binary 构造函数，用于按 Base64 内容创建 BSON 二进制类型
 *
 * @author oyzh
 * @since 2026-06-08
 */
public class MongoScriptBinaryFcuntion {

    /**
     * 按 Base64 内容创建二进制数据，子类型默认为 0
     *
     * @return BSON 二进制数据
     */
    public Binary createFromBase64() {
        return this.createFromBase64("", 0);
    }

    /**
     * 按 Base64 内容和子类型创建二进制数据
     *
     * @param base64 Base64 编码内容
     * @param type   二进制子类型
     * @return BSON 二进制数据
     */
    public Binary createFromBase64(String base64, int type) {
        byte[] data = Base64Util.decode(base64);
        return new Binary((byte) type, data);
    }
}
