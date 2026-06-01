package cn.oyzh.easymongo.util;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.mongo.MongoClient;

/**
 * db连接工具类
 *
 * @author oyzh
 * @since 2023/07/01
 */
public class MongoConnectUtil {

    /**
     * 测试连接
     *
     * @param view   页面
     * @param dbInfo db信息
     */
    public static void testConnect(StageAdapter view, MongoConnect dbInfo) {
        ThreadUtil.start(() -> {
            try {
                view.disable();
                view.waitCursor();
                view.appendTitle("==连接测试中...");
                MongoClient client = new MongoClient(dbInfo);
                if (client != null) {
                    client.start();
                    if (client.isConnected()) {
                        client.close();
                        MessageBox.okToast("连接成功！");
                    } else {
                        MessageBox.warn("连接失败，请检查地址是否有效！");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                view.enable();
                view.defaultCursor();
                view.restoreTitle();
            }
        });
    }

    /**
     * 关闭连接
     *
     * @param client db客户端
     * @param async  是否异步
     */
    public static void close(MongoClient client, boolean async) {
        try {
            if (client != null && client.isConnected()) {
                Runnable func = client::close;
                if (async) {
                    ThreadUtil.start(func);
                } else {
                    func.run();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
