package cn.oyzh.easymongo.data.file;

import cn.oyzh.common.file.LineFileWriter;
import cn.oyzh.easymongo.data.config.MongoDataExportConfig;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;
import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoDataUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * MongoDB 数据导出的 JS 脚本类型文件写入器
 *
 * @author oyzh
 * @since 2024-09-04
 */
public class MongoJsTypeFileWriter extends MongoTypeFileWriter {

    /**
     * 字段列表
     */
    private MongoColumns columns;

    /**
     * 导出配置
     */
    private MongoDataExportConfig config;

    /**
     * 文件写入器
     */
    private LineFileWriter writer;

    /**
     * 构造 JS 脚本类型文件写入器
     *
     * @param filePath 导出文件路径
     * @param config   导出配置
     * @param columns  字段列表
     * @throws FileNotFoundException 文件未找到异常
     */
    public MongoJsTypeFileWriter(String filePath, MongoDataExportConfig config, MongoColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.getCharset());
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        MongoRecord record = new MongoRecord(this.columns);
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            record.putValue(entry.getKey(), entry.getValue());
        }
        String script = MongoDataUtil.toInsertScript(record);
        this.writer.writeLine(script);
    }

    @Override
    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
            this.writer = null;
            this.config = null;
            this.columns = null;
        }
    }

    @Override
    public Object parameterized(MongoColumn column, Object value, MongoDataExportConfig config) {
        if (value == null) {
            return null;
        }
        return super.parameterized(column, value, config);
    }
}
