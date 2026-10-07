package cn.oyzh.easymongo.data.file;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB 数据导入文件读取器基类，定义读取文件所需的基础行为
 *
 * @author oyzh
 * @since 2024-09-03
 */
public abstract class MongoTypeFileReader implements Closeable {

    /**
     * 待读取的文件
     */
    private final File file;

    /**
     * 构造文件读取器
     *
     * @param file 待读取的文件
     */
    public MongoTypeFileReader(File file) {
        this.file = file;
    }

    /**
     * 获取待读取的文件
     *
     * @return 待读取的文件
     */
    public File getFile() {
        return file;
    }

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    protected void init() throws Exception {

    }

    /**
     * 读取一个对象
     *
     * @return 对象数据
     * @throws Exception 异常
     */
    public abstract Map<String, Object> readObject() throws Exception;

    /**
     * 读取指定数量的对象
     *
     * @param count 读取数量
     * @return 对象列表
     * @throws Exception 异常
     */
    public List<Map<String, Object>> readObjects(int count) throws Exception {
        // 数据列表
        List<Map<String, Object>> records = new ArrayList<>();
        // 读取数据
        while (records.size() < count) {
            Map<String, Object> item = this.readObject();
            if (item == null) {
                break;
            }
            records.add(item);
        }
        return records;
    }

    /**
     * 解析单行文本为字段列表
     *
     * @param line           行文本
     * @param txtIdentifier  文本标识符
     * @param fieldSeparator 字段分隔符
     * @return 字段列表
     * @throws IOException IO 异常
     */
    protected List<String> parseLine(String line, char txtIdentifier, char fieldSeparator) throws IOException {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean txtStart = false;
        try (StringReader reader = new StringReader(line)) {
            while (reader.ready()) {
                int i = reader.read();
                if (i == -1) {
                    break;
                }
                char c = (char) i;
                if (txtStart && c == fieldSeparator) {
                    txtStart = false;
                    continue;
                }
                if (c == txtIdentifier) {
                    if (txtStart) {
                        list.add(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        txtStart = true;
                    }
                } else if (txtStart) {
                    sb.append(c);
                }
            }
        }
        return list;
    }

}
