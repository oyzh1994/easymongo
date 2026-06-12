package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.date.DateHelper;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.NumberUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.exception.MongoException;
import cn.oyzh.easymongo.mongo.condition.MongoConditionUtil;
import cn.oyzh.easymongo.query.MongoExecuteResult;
import cn.oyzh.easymongo.query.MongoQueryResults;
import cn.oyzh.easymongo.script.MongoScriptCursor;
import cn.oyzh.easymongo.script.MongoScriptEngine;
import cn.oyzh.easymongo.script.MongoScriptFindCursor;
import cn.oyzh.easymongo.script.MongoScriptParser;
import cn.oyzh.easymongo.util.MongoRecordUtil;
import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoNamespace;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListCollectionNamesIterable;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import org.bson.types.ObjectId;

import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * db客户端封装
 *
 * @author oyzh
 * @since 2023/11/06
 */
public class MongoClient implements Closeable {

    /**
     * db信息
     */
    protected MongoConnect shellConnect;

    public MongoClient(MongoConnect value) {
        this.shellConnect = value;
    }

    public String connectName() {
        return this.shellConnect.getName();
    }

    public boolean isConnected() {
        return this.state.get() != null && this.state.get().isConnected();
    }

    public boolean isConnecting() {
        return this.state.get() == MongoConnState.CONNECTING;
    }

    @Override
    public void close() {
        try {
            if (this.mongoClient != null) {
                this.mongoClient.close();
            }
            this.state.set(MongoConnState.CLOSED);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("Zookeeper client close error.", ex);
        }
    }

    /**
     * 连接状态
     */
    private final SimpleObjectProperty<MongoConnState> state = new SimpleObjectProperty<>();

    /**
     * 当前状态监听器
     */
    private final ChangeListener<MongoConnState> stateListener = (state1, state2, state3) -> {
    };

    public ObjectProperty<MongoConnState> stateProperty() {
        return this.state;
    }

    /**
     * 初始化连接
     *
     * @return 连接
     */
    private String initHost() {
        // 连接地址
        String host = this.shellConnect.hostIp() + ":" + this.shellConnect.hostPort();
        return host;
    }

    private com.mongodb.client.MongoClient mongoClient;

    private MongoScriptEngine engine;

    /**
     * 创建shell引擎
     *
     * @return 结果
     */
    public MongoScriptEngine shellEngine() {
        if (this.engine == null) {
            this.engine = new MongoScriptEngine(this.mongoClient);
        }
        return this.engine;
    }

    /**
     * 初始化客户端
     */
    private void initClient() {
        // 连接信息
        String host = this.initHost();
        // 密码认证
        if (this.shellConnect.isPasswordAuth()) {
            String hostIp = host.split(":")[0];
            int port = Integer.parseInt(host.split(":")[1]);
            String user = this.shellConnect.getUser();
            String database = this.shellConnect.getAuthDatabase();
            String password = this.shellConnect.getPassword();
            MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(hostIp, port))))
                    .credential(credential)
                    .build();
            // 创建客户端
            this.mongoClient = MongoClients.create(settings);
        } else {
            String connStr = "mongodb://" + host;
            // 创建客户端
            this.mongoClient = MongoClients.create(connStr);
        }
    }

    public void start() {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        // 初始化客户端
        this.initClient();
        try {
            // 开始连接时间
            final AtomicLong starTime = new AtomicLong(System.currentTimeMillis());
            // 更新连接状态
            this.state.set(MongoConnState.CONNECTING);
            // 检查连接
            this.mongoClient.listDatabases();
            // 更新连接状态
            this.state.set(MongoConnState.CONNECTED);
            // 开始连接时间
            starTime.set(System.currentTimeMillis());
        } catch (Exception ex) {
            this.state.set(MongoConnState.FAILED);
            JulLog.warn("Mongo client start error", ex);
            throw new MongoException(ex);
        }
    }

    public MongoDatabase database(String dbName) {
        MongoDatabase database1 = new MongoDatabase();
        database1.setName(dbName);
        return database1;
    }

    /**
     * 列举数据库
     *
     * @return 结果
     */
    public List<MongoDatabase> listDatabases() {
        List<MongoDatabase> databases = new ArrayList<>();
        ListDatabasesIterable<Document> documents = this.mongoClient.listDatabases();
        for (Document document : documents) {
            MongoDatabase database = new MongoDatabase();
            String name = document.getString("name");
            database.setName(name);
            Object sizeOnDisk = document.get("sizeOnDisk");
            if (sizeOnDisk instanceof Number number) {
                database.setSizeOnDisk(number.doubleValue());
            }
            databases.add(database);
        }
        return databases;
    }

    /**
     * 列举数据库名称
     *
     * @return 结果
     */
    public List<String> listDatabaseNames() {
        MongoIterable<String> iterable = this.mongoClient.listDatabaseNames();
        List<String> list = new ArrayList<>();
        for (String s : iterable) {
            list.add(s);
        }
        return list;
    }

    private com.mongodb.client.MongoCollection<Document> collection(String dbName, String collectionName) {
        return this.mongoClient.getDatabase(dbName).getCollection(collectionName);
    }

    public void createDatabase(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        database.createCollection("_empty_");
    }

    public boolean existDatabase(String dbName) {
        try {
            com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
            MongoIterable<String> iterable = database.listCollectionNames();
            return iterable.first() != null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean alterDatabase(MongoDatabase database) {
        return false;
    }

    public boolean dropDatabase(String dbName) {
        try {
            com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
            database.drop();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 删除集合
     *
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     */
    public void dropCollection(String dbName, String collectionName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        collection.drop();
    }

    /**
     * 列举集合
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public List<MongoCollection> listCollections(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        List<MongoCollection> collections = new ArrayList<>();
        for (String name : database.listCollectionNames()) {
            if (!MongoRecordUtil.isCollection(name)) {
                continue;
            }
            MongoCollection collection = new MongoCollection();
            collection.setDbName(dbName);
            collection.setName(name);
            collections.add(collection);
        }
        collections = collections.stream().sorted(Comparator.comparing(MongoCollection::getName)).toList();
        return collections;
    }

    /**
     * 列举集合名称
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public List<String> listCollectionNames(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        ListCollectionNamesIterable iterable = database.listCollectionNames();
        List<String> list = new ArrayList<>();
        for (String name : iterable) {
            if (MongoRecordUtil.isCollection(name)) {
                list.add(name);
            }
        }
        return list;
    }

    /**
     * 查询集合记录
     *
     * @param param 参数
     * @return 结果
     */
    public List<MongoRecord> selectCollectionRecords(MongoSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        Bson filters = MongoConditionUtil.buildCondition(param.getFilters());
        FindIterable<Document> iterable = collection.find(filters);
        if (param.getStart() != null) {
            int skip = Math.toIntExact(param.getStart());
            iterable = iterable.skip(skip);
        }
        if (param.getLimit() != null) {
            int limit = Math.toIntExact(param.getLimit());
            iterable = iterable.limit(limit);
        }
        //        List<MongoRecord> records = new ArrayList<>();
        //        for (Document document : iterable) {
        //            Set<String> cols = document.keySet();
        //            if (!cols.isEmpty()) {
        //                MongoColumns columns = new MongoColumns();
        //                MongoRecord record = new MongoRecord(columns);
        //                for (String col : cols) {
        //                    Object val = document.get(col);
        //                    MongoColumn column = new MongoColumn();
        //                    column.setName(col);
        //                    column.setDbName(dbName);
        //                    column.setCollectionName(collectionName);
        //                    column.setType(MongoUtil.getType(val));
        //                    columns.add(column);
        //                    record.putValue(column, val);
        //                }
        //                records.add(record);
        //            }
        //        }
        return MongoRecordUtil.docToRecord(dbName, collectionName, iterable);
    }

    /**
     * 查询集合记录数量
     *
     * @param param 参数
     * @return 结果
     */
    public long selectCollectionRecordCount(MongoSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        Bson filters = MongoConditionUtil.buildCondition(param.getFilters());
        return collection.countDocuments(filters);
    }

    /**
     * 新增集合记录
     *
     * @param record 数据
     * @return 结果
     */
    public BsonValue insertCollectionRecord(MongoRecord record) {
        String dbName = record.getColumns().getFirst().getDbName();
        String collectionName = record.getColumns().getFirst().getCollectionName();
        Document document = new Document();
        for (MongoColumn column : record.getColumns()) {
            Object value = record.getValue(column.getName());
            if (column.is_id() && value == null) {
                continue;
            }
            document.append(column.getName(), value);
        }
        if (dbName != null) {
            com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
            com.mongodb.client.MongoCollection<Document> collection = database.getCollection(collectionName);
            InsertOneResult result = collection.insertOne(document);
            return result.getInsertedId();
        }
        return null;
    }

    /**
     * 新增多条集合记录
     *
     * @param records 数据列表
     * @return 结果
     */
    public List<BsonValue> insertCollectionRecord(List<MongoRecord> records) {
        if (CollectionUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        String dbName = records.getFirst()._idColumn().getDbName();
        String collectionName = records.getFirst()._idColumn().getCollectionName();
        List<Document> documents = new ArrayList<>();
        for (MongoRecord record : records) {
            Document document = new Document();
            for (String col : record.columns()) {
                document.append(col, record.getValue(col));
            }
            documents.add(document);
        }
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        com.mongodb.client.MongoCollection<Document> collection = database.getCollection(collectionName);
        InsertManyResult result = collection.insertMany(documents);
        Map<Integer, BsonValue> map = result.getInsertedIds();
        return new ArrayList<>(map.values());
    }

    /**
     * 删除集合记录
     *
     * @param record 数据
     * @return 结果
     */
    public long deleteCollectionRecord(MongoRecord record) {
        MongoColumn column = record._idColumn();
        if (column == null) {
            throw new IllegalArgumentException("_id");
        }
        String dbName = column.getDbName();
        String collectionName = column.getCollectionName();
        Object _id = record._idValue();
        Bson filter = Filters.eq(MongoUtil.ID, _id);
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        DeleteResult result = collection.deleteOne(filter);
        return result.getDeletedCount();
    }

    /**
     * 创建集合
     *
     * @param collection 集合
     */
    public void createCollection(MongoCollection collection) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(collection.getDbName());
        database.createCollection(collection.getName());
    }

    /**
     * 清空集合
     *
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     * @return 结果
     */
    public long clearCollection(String dbName, String collectionName) {
        com.mongodb.client.MongoCollection<Document> collection1 = this.collection(dbName, collectionName);
        DeleteResult result = collection1.deleteMany(new Document());
        return result.getDeletedCount();
    }

    /**
     * 重命名集合
     *
     * @param dbName  数据库名称
     * @param oldName 旧名称
     * @param newName 新名称
     */
    public void renameCollection(String dbName, String oldName, String newName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, oldName);
        MongoNamespace namespace = new MongoNamespace(dbName, newName);
        collection.renameCollection(namespace);
    }

    /**
     * 更新集合记录
     *
     * @param record 数据
     * @return 结果
     */
    public long updateCollectionRecord(MongoRecord record) {
        MongoColumn column = record._idColumn();
        if (column == null) {
            throw new IllegalArgumentException("_id");
        }
        String dbName = column.getDbName();
        String collectionName = column.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection1 = this.collection(dbName, collectionName);
        Object _id = record._idValue();
        Bson filter = Filters.eq(MongoUtil.ID, _id);
        FindIterable<Document> iterable = collection1.find(filter);
        Document document = iterable.first();
        if (document == null) {
            return 0;
        }
        Bson update = null;
        for (MongoColumn mongoColumn : record.getColumns()) {
            if (mongoColumn.is_id()) {
                continue;
            }
            String colName = mongoColumn.getName();
            Bson bson = Updates.set(colName, record.getValue(colName));
            if (update == null) {
                update = bson;
            } else {
                update = Updates.combine(update, bson);
            }
        }

        for (String colName : document.keySet()) {
            if (record.column(colName) == null) {
                Bson bson = Updates.unset(colName);
                update = Updates.combine(update, bson);
            }
        }

        if (update != null) {
            UpdateResult result = collection1.updateOne(filter, update);
            return result.getMatchedCount();
        }
        return 0;
    }

    /**
     * 列举存储桶
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public List<MongoBucket> listBuckets(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoIterable<String> collectionNames = database.listCollectionNames();
        List<MongoBucket> buckets = new ArrayList<>();
        for (String collectionName : collectionNames) {
            if (!MongoRecordUtil.isBucket(collectionName)) {
                continue;
            }
            MongoBucket gridFS = new MongoBucket();
            gridFS.setDbName(dbName);
            gridFS.setName(collectionName.substring(0, collectionName.lastIndexOf(".")));
            buckets.add(gridFS);
        }
        buckets = buckets.stream().sorted(Comparator.comparing(MongoBucket::getName)).toList();
        return buckets;
    }

    /**
     * 列举存储桶名称
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public List<String> listBucketNames(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoIterable<String> collectionNames = database.listCollectionNames();
        List<String> buckets = new ArrayList<>();
        for (String collectionName : collectionNames) {
            if (!MongoRecordUtil.isBucket(collectionName)) {
                continue;
            }
            buckets.add(collectionName);
        }
        return buckets;
    }

    /**
     * 获取存储桶
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     */
    public GridFSBucket bucket(String dbName, String bucketName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        return GridFSBuckets.create(database, bucketName);
    }

    /**
     * 创建存储桶
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     */
    public void createBucket(String dbName, String bucketName) {
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        // 需要上传一次数据，不然桶不会出现
        ByteArrayInputStream bis = new ByteArrayInputStream(new byte[]{});
        ObjectId _id = bucket.uploadFromStream("_empty_", bis);
        // 删除此数据
        this.deleteBucketRecord(dbName, bucketName, new BsonObjectId(_id));
    }

    /**
     * 删除存储桶
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     */
    public void dropBucket(String dbName, String bucketName) {
        this.dropCollection(dbName, bucketName + ".files");
    }

    /**
     * 清除存储桶
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     */
    public void clearBucket(String dbName, String bucketName) {
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        bucket.find().forEach(file -> bucket.delete(file.getObjectId()));
    }

    /**
     * 查询存储桶记录
     *
     * @param param 参数
     * @return 结果
     */
    public List<MongoRecord> selectBucketRecords(MongoSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        GridFSBucket fsBucket = this.bucket(dbName, collectionName);
        int skip = Math.toIntExact(param.getStart());
        int limit = Math.toIntExact(param.getLimit());

        Bson filters = MongoConditionUtil.buildCondition(param.getFilters());
        GridFSFindIterable iterable = fsBucket.find(filters).limit(limit).skip(skip);
        List<MongoRecord> records = new ArrayList<>();
        MongoColumns columns = new MongoColumns();
        MongoColumn idColumn = new MongoColumn(MongoUtil.ID, I18nHelper.id());
        columns.add(idColumn);
        MongoColumn fileNameColumn = new MongoColumn("filename", I18nHelper.fileName());
        columns.add(fileNameColumn);
        MongoColumn lengthColumn = new MongoColumn("length", I18nHelper.length());
        columns.add(lengthColumn);
        MongoColumn chunkSizeColumn = new MongoColumn("chunkSize", I18nHelper.chunkSize());
        columns.add(chunkSizeColumn);
        MongoColumn uploadDateColumn = new MongoColumn("uploadDate", I18nHelper.uploadDate());
        columns.add(uploadDateColumn);
        for (GridFSFile file : iterable) {
            MongoRecord record = new MongoRecord(columns, true);
            record.putValue(idColumn, file.getId());
            record.putValue(fileNameColumn, file.getFilename());
            record.putValue(lengthColumn, NumberUtil.formatSize(file.getLength(), 2));
            record.putValue(chunkSizeColumn, NumberUtil.formatSize(file.getChunkSize(), 2));
            record.putValue(uploadDateColumn, DateHelper.formatDateTimeSimple(file.getUploadDate()));
            records.add(record);
        }
        return records;
    }

    /**
     * 查询存储桶记录数量
     *
     * @param param 参数
     * @return 结果
     */
    public long selectBucketRecordCount(MongoSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName + ".files");
        Bson filters = MongoConditionUtil.buildCondition(param.getFilters());
        return collection.countDocuments(filters);
    }

    /**
     * 查询单个记录
     *
     * @param dbName     数据库名称
     * @param bucketName 存储桶名称
     * @param _id        数据id
     * @return 结果
     */
    public MongoRecord selectBucketRecord(String dbName, String bucketName, BsonValue _id) {
        if (_id == null) {
            throw new IllegalArgumentException("_id");
        }
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        Bson filters = Filters.eq(MongoUtil.ID, _id);
        GridFSFindIterable iterable = bucket.find(filters).limit(1);
        MongoColumns columns = new MongoColumns();
        MongoColumn idColumn = new MongoColumn(MongoUtil.ID, I18nHelper.id());
        columns.add(idColumn);
        MongoColumn fileNameColumn = new MongoColumn("filename", I18nHelper.fileName());
        columns.add(fileNameColumn);
        MongoColumn lengthColumn = new MongoColumn("length", I18nHelper.length());
        columns.add(lengthColumn);
        MongoColumn chunkSizeColumn = new MongoColumn("chunkSize", I18nHelper.chunkSize());
        columns.add(chunkSizeColumn);
        MongoColumn uploadDateColumn = new MongoColumn("uploadDate", I18nHelper.uploadDate());
        columns.add(uploadDateColumn);
        GridFSFile file = iterable.first();
        if (file != null) {
            MongoRecord record = new MongoRecord(columns, true);
            record.putValue(idColumn, file.getId());
            record.putValue(fileNameColumn, file.getFilename());
            record.putValue(lengthColumn, NumberUtil.formatSize(file.getLength(), 2));
            record.putValue(chunkSizeColumn, NumberUtil.formatSize(file.getChunkSize(), 2));
            record.putValue(uploadDateColumn, DateHelper.formatDateTimeSimple(file.getUploadDate()));
            return record;
        }
        return null;
    }

    /**
     * 存储桶字段列表
     *
     * @return 结果
     */
    public MongoColumns bucketColumns() {
        MongoColumns columns = new MongoColumns();
        MongoColumn idColumn = new MongoColumn("_id", I18nHelper.id());
        columns.add(idColumn);
        MongoColumn fileNameColumn = new MongoColumn("filename", I18nHelper.fileName());
        columns.add(fileNameColumn);
        MongoColumn lengthColumn = new MongoColumn("length", I18nHelper.length());
        columns.add(lengthColumn);
        MongoColumn chunkSizeColumn = new MongoColumn("chunkSize", I18nHelper.chunkSize());
        columns.add(chunkSizeColumn);
        MongoColumn uploadDateColumn = new MongoColumn("uploadDate", I18nHelper.uploadDate());
        columns.add(uploadDateColumn);
        return columns;
    }

    /**
     * 上传存储桶记录
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     * @param file       文件
     */
    public ObjectId uploadBucketRecord(String dbName, String bucketName, File file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        FileInputStream fis = new FileInputStream(file);
        ObjectId objectId;
        try (fis) {
            objectId = bucket.uploadFromStream(file.getName(), fis);
        }
        return objectId;
    }

    /**
     * 下载存储桶记录
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     * @param _id        数据id
     * @param file       文件
     */
    public void downloadBucketRecord(String dbName, String bucketName, BsonValue _id, File file) throws FileNotFoundException {
        if (_id == null) {
            throw new IllegalArgumentException("_id");
        }
        if (file == null) {
            throw new IllegalArgumentException("file");
        }
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        FileOutputStream fos = new FileOutputStream(file);
        bucket.downloadToStream(_id, fos);
    }

    /**
     * 删除存储桶记录
     *
     * @param dbName     数据库名称
     * @param bucketName 桶名称
     * @param _id        数据id
     * @return 结果
     */
    public long deleteBucketRecord(String dbName, String bucketName, BsonValue _id) {
        if (_id == null) {
            throw new IllegalArgumentException("_id");
        }
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        bucket.delete(_id);
        return 1;
    }

    public List<? extends MongoColumn> selectColumns(MongoSelectRecordParam param) {
        List<MongoRecord> records = this.selectCollectionRecords(param);
        return MongoRecordUtil.columns(records);
    }

    private String version;

    public String selectVersion() {
        if (this.version == null) {
            Document buildInfo = this.mongoClient.getDatabase("admin")
                    .runCommand(new Document("buildInfo", 1));
            this.version = buildInfo.getString("version");
        }
        return this.version;
    }

    public MongoConnect getShellConnect() {
        return this.shellConnect;
    }

    /**
     * 执行单段脚本
     *
     * @param dbName 数据库名称
     * @param script 脚本
     * @return 结果
     */
    public MongoExecuteResult executeSingleScript(String dbName, String script) {
        this.shellEngine().db(dbName);
        MongoExecuteResult result = new MongoExecuteResult();
        result.setScript(script);
        long start = System.currentTimeMillis();
        try {
            Object obj = this.shellEngine().eval(script);
            this.parseResult(result, obj, dbName, null);
        } catch (ScriptException ex) {
            ex.printStackTrace();
            result.setSuccess(false);
            result.setMsg(ex.getMessage());
        }
        result.setMsg("ok");
        long end = System.currentTimeMillis();
        result.setUsed(end - start);
        return result;
    }

    /**
     * 解析结果
     *
     * @param result 结果
     * @param obj    对象
     * @param dbName 数据库名称
     */
    private void parseResult(MongoExecuteResult result, Object obj, String dbName, String collectionName) {
        if (obj instanceof MongoScriptFindCursor cursor) {
            parseResult(result, cursor.toArray(), cursor.getDbName(), cursor.getCollectionName());
        } else if (obj instanceof MongoScriptCursor cursor) {
            parseResult(result, cursor.toArray(), dbName, collectionName);
        } else if (obj instanceof List<?> list) {
            result.setSuccess(true);
            List<MongoRecord> records = new ArrayList<>();
            for (Object o : list) {
                MongoRecord record = toMongoRecord(o, dbName, collectionName);
                if (record != null) {
                    records.add(record);
                }
            }
            result.parseResult(records);
        } else if (obj instanceof DeleteResult result1) {
            result.setSuccess(true);
            result.setUpdateCount(result1.getDeletedCount());
        } else if (obj instanceof InsertOneResult result1) {
            result.setSuccess(true);
            if (result1.getInsertedId() != null) {
                result.setUpdateCount(1);
            }
        } else if (obj instanceof InsertManyResult result1) {
            result.setSuccess(true);
            result.setUpdateCount(result1.getInsertedIds().size());
        } else if (obj instanceof UpdateResult result1) {
            result.setSuccess(true);
            result.setUpdateCount(result1.getModifiedCount());
        } else {
            result.setSuccess(true);
            MongoRecord record = toMongoRecord(obj, dbName, collectionName);
            if (record != null) {
                result.parseResult(List.of(record));
            }
        }
    }

    /**
     * 转换为mongo记录
     *
     * @param obj            对象
     * @param dbName         数据库名称
     * @param collectionName 集合名称
     */
    private MongoRecord toMongoRecord(Object obj, String dbName, String collectionName) {
        if (obj instanceof Document document) {
            return MongoRecordUtil.docToRecord(dbName, collectionName, document);
        }
        if (obj != null) {
            MongoColumns columns = new MongoColumns();
            MongoColumn column = new MongoColumn(I18nHelper.result());
            column.setDbName(dbName);
            column.setCollectionName(collectionName);
            columns.add(column);
            MongoRecord record = new MongoRecord(columns);
            record.putValue(column, JSONUtil.toJson(obj));
            return record;
        }
        return null;
    }

    /**
     * 执行脚本
     *
     * @param dbName 数据库名称
     * @param script 脚本
     * @return 结果
     */
    public MongoQueryResults<MongoExecuteResult> executeScript(String dbName, String script) {
        this.shellEngine().db(dbName);
        MongoQueryResults<MongoExecuteResult> results = new MongoQueryResults<>();
        try {
            MongoScriptParser parser = MongoScriptParser.getParser(script);
            List<String> sqlList = parser.parseScript();
            for (String sql1 : sqlList) {
                MongoExecuteResult result = this.executeSingleScript(dbName, sql1);
                results.addResult(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            results.parseError(ex);
        }
        return results;
    }

    /**
     * 执行脚本
     *
     * @param dbName 数据库名称
     * @return 结果
     */
    public List<MongoFunction> listFunctions(String dbName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        List<MongoFunction> functions = new ArrayList<>();
        FindIterable<Document> iter = collection.find();
        for (Document document : iter) {
            MongoFunction function = new MongoFunction();
            Object value = document.get("value");
            if (value instanceof Code code) {
                function.setName(document.getString("_id"));
                function.setCode(code.getCode());
                function.setDbName(dbName);
                functions.add(function);
            }
        }
        return functions;
    }

    public BsonValue createFunction(String dbName, String functionName, String code) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        Document funcDoc = new Document()
                .append("_id", functionName)
                .append("value", new Code(code));
        InsertOneResult result = collection.insertOne(funcDoc);
        return result.getInsertedId();
    }

    public boolean alertFunction(String dbName, String functionName, String code) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        Bson filter = Filters.eq("_id", functionName);
        Bson update = Updates.set("value", new Code(code));
        UpdateResult result = collection.updateOne(filter, update);
        return result.getMatchedCount() == 1;
    }

    public MongoFunction selectFunction(String dbName, String functionName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        Bson filter = Filters.eq("_id", functionName);
        FindIterable<Document> iter = collection.find(filter);
        Document document = iter.first();
        if (document != null) {
            MongoFunction function = new MongoFunction();
            Code code = (Code) document.get("value");
            function.setName(document.getString("_id"));
            function.setCode(code.getCode());
            function.setDbName(dbName);
            return function;
        }
        return null;
    }

    public boolean dropFunction(String dbName, String functionName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        Bson filter = Filters.eq("_id", functionName);
        Document document = collection.findOneAndDelete(filter);
        return document != null;
    }

    public boolean renameFunction(String dbName, String oldName, String newName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        Bson filter = Filters.eq("_id", oldName);
        FindIterable<Document> iter = collection.find(filter);
        Document document = iter.first();
        if (document != null) {
            Document newFunc = new Document()
                    .append("_id", newName)
                    .append("value", document.get("value"));
            collection.insertOne(newFunc);
            collection.deleteOne(filter);

            return true;
        }
        return false;
    }

    public long functionSize(String dbName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, MongoUtil.SYSTEM_JS);
        return collection.countDocuments();
    }

    public void addStateListener(ChangeListener<MongoConnState> stateChangeListener) {
        this.state.addListener(stateChangeListener);
    }

    public boolean isClosed() {
        return !this.isConnected() && !this.isConnecting();
    }

    public String iid() {
        return this.shellConnect.getId();
    }

}
