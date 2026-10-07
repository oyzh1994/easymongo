package cn.oyzh.easymongo.data.file;

import cn.oyzh.common.file.LineFileWriter;
import cn.oyzh.easymongo.data.config.MongoDataExportConfig;
import cn.oyzh.easymongo.mongo.MongoColumn;
import cn.oyzh.easymongo.mongo.MongoColumns;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * MongoDB 数据导出的 CSV 类型文件写入器
 *
 * @author oyzh
 * @since 2024-09-04
 */
public class MongoCsvTypeFileWriter extends MongoTypeFileWriter {

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
    private final LineFileWriter writer;

    /**
     * 构造 CSV 类型文件写入器
     *
     * @param filePath 导出文件路径
     * @param config   导出配置
     * @param columns  字段列表
     * @throws FileNotFoundException 文件未找到异常
     */
    public MongoCsvTypeFileWriter(String filePath, MongoDataExportConfig config, MongoColumns columns) throws FileNotFoundException {
        this.columns = columns;
        this.config = config;
        this.writer = LineFileWriter.create(filePath, config.getCharset());
    }

    @Override
    public void writeHeader() throws Exception {
        this.writer.write(this.formatLine(this.columns.columnNames(), ",",
                this.config.getTxtIdentifier(),
                this.config.getRecordSeparator()));
    }

    @Override
    public void writeObject(Map<String, Object> object) throws Exception {
        Object[] values = new Object[this.columns.size()];
        for (Map.Entry<String, Object> entry : object.entrySet()) {
            int index = this.columns.index(entry.getKey());
            MongoColumn column = this.columns.column(entry.getKey());
            Object val = this.parameterized(column, entry.getValue(), this.config);
            values[index] = val;
        }
        this.writer.write(this.formatLine(values, ",",
                this.config.getTxtIdentifier(),
                this.config.getRecordSeparator()));
    }

    @Override
    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.close();
            this.config = null;
            this.columns = null;
        }
    }
}
