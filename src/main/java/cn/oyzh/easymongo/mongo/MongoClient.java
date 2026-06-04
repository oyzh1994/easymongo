package cn.oyzh.easymongo.mongo;

import cn.oyzh.common.date.DateHelper;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.NumberUtil;
import cn.oyzh.easymongo.domain.MongoConnect;
import cn.oyzh.easymongo.exception.MongoException;
import cn.oyzh.easymongo.mongo.condition.MysqlConditionUtil;
import cn.oyzh.easymongo.util.MongoUtil;
import cn.oyzh.i18n.I18nHelper;
import com.mongodb.client.FindIterable;
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
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public MongoConnect getDbConnect() {
        return this.shellConnect;
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

    /**
     * 初始化客户端
     */
    private void initClient() {
        // 连接信息
        String host = this.initHost();
        String connStr = "mongodb://";
        if (this.shellConnect.isPasswordAuth()) {
            connStr += this.shellConnect.getUser() + ":" + this.shellConnect.getPassword() + "@";
        }
        connStr += host;
        if (this.shellConnect.isPasswordAuth()) {
            connStr += "/" + this.shellConnect.getAuthDatabase();
        }
        // 创建客户端
        this.mongoClient = MongoClients.create(connStr);
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

    public List<MongoDatabase> databases() {
        List<MongoDatabase> databases = new ArrayList<>();
        ListDatabasesIterable<Document> documents = this.mongoClient.listDatabases();
        for (Document document : documents) {
            MongoDatabase database = new MongoDatabase();
            String name = document.getString("name");
            Long sizeOnDisk = document.getLong("sizeOnDisk");
            database.setName(name);
            database.setSizeOnDisk(sizeOnDisk);
            databases.add(database);
        }
        return databases;
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

    public void dropCollection(String dbName, String collectionName) {
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        collection.drop();
    }

    public List<MongoCollection> collections(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        List<MongoCollection> collections = new ArrayList<>();
        for (String name : database.listCollectionNames()) {
            if (name.endsWith(".files")) {
                continue;
            }
            MongoCollection collection = new MongoCollection();
            collection.setDbName(dbName);
            collection.setName(name);
            collections.add(collection);
        }
        return collections;
    }

    public List<MongoRecord> selectRecords(MysqlSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        int skip = Math.toIntExact(param.getStart());
        int limit = Math.toIntExact(param.getLimit());

        Bson filters = MysqlConditionUtil.buildCondition(param.getFilters());
        FindIterable<Document> iterable = collection.find(filters).limit(limit).skip(skip);
        List<MongoRecord> records = new ArrayList<>();
        for (Document document : iterable) {
            Set<String> cols = document.keySet();
            if (!cols.isEmpty()) {
                MongoColumns columns = new MongoColumns();
                MongoRecord record = new MongoRecord(columns);
                for (String col : cols) {
                    Object val = document.get(col);
                    MongoColumn column = new MongoColumn();
                    column.setName(col);
                    column.setDbName(dbName);
                    column.setCollectionName(collectionName);
                    column.setType(MongoUtil.getType(val));
                    columns.add(column);
                    record.putValue(column, val);
                }
                records.add(record);
            }
        }
        return records;
    }

    public ObjectId insertRecord(MongoRecordData recordData) {
        String dbName = null;
        String collectionName = null;
        Document document = new Document();
        for (Map.Entry<MongoColumn, Object> entry : recordData.entries()) {
            Object value = entry.getValue();
            MongoColumn column = entry.getKey();
            if (column.is_id()) {
                continue;
            }
            if (dbName == null) {
                dbName = column.getDbName();
            }
            if (collectionName == null) {
                collectionName = column.getCollectionName();
            }
            document.append(column.getName(), value);
        }
        if (dbName != null) {
            com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
            com.mongodb.client.MongoCollection<Document> collection = database.getCollection(collectionName);
            InsertOneResult result = collection.insertOne(document);
            BsonValue value = result.getInsertedId();
            if (value != null) {
                return value.asObjectId().getValue();
            }
        }
        return null;
    }

    public long deleteRecord(MongoRecordData recordData) {
        MongoColumn column = recordData.column(MongoUtil.ID);
        if (column == null) {
            throw new IllegalArgumentException("_id");
        }
        String dbName = column.getDbName();
        String collectionName = column.getCollectionName();
        ObjectId _id = recordData.id();
        Bson filter = Filters.eq(MongoUtil.ID, _id);
        com.mongodb.client.MongoCollection<Document> collection = this.collection(dbName, collectionName);
        DeleteResult result = collection.deleteOne(filter);
        return result.getDeletedCount();
    }

    public void createCollection(MongoCollection collection) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(collection.getDbName());
        database.createCollection(collection.getName());
    }

    public long clearCollection(String dbName, String collectionName) {
        com.mongodb.client.MongoCollection<Document> collection1 = this.collection(dbName, collectionName);
        DeleteResult result = collection1.deleteMany(new Document());
        return result.getDeletedCount();
    }

    public long updateRecord(MongoRecordData recordData) {
        MongoColumn column = recordData.column(MongoUtil.ID);
        if (column == null) {
            throw new IllegalArgumentException("_id");
        }
        String dbName = column.getDbName();
        String collectionName = column.getCollectionName();
        com.mongodb.client.MongoCollection<Document> collection1 = this.collection(dbName, collectionName);
        ObjectId _id = recordData.id();
        Bson filter = Filters.eq(MongoUtil.ID, _id);
        FindIterable<Document> iterable = collection1.find(filter);
        Document document = iterable.first();
        if (document == null) {
            return 0;
        }
        Bson update = null;
        for (Map.Entry<MongoColumn, Object> entry : recordData.entries()) {
            if (entry.getKey().is_id()) {
                continue;
            }
            String colName = entry.getKey().getName();
            Bson bson = Updates.set(colName, entry.getValue());
            if (update == null) {
                update = bson;
            } else {
                update = Updates.combine(update, bson);
            }
        }

        for (String colName : document.keySet()) {
            if (recordData.column(colName) == null) {
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


    private GridFSBucket bucket(String dbName, String bucketName) {
        bucketName = bucketName.endsWith(".files") ? bucketName : bucketName + ".files";
        return createBucket(dbName, bucketName);
    }

    public List<MongoBucket> buckets(String dbName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        MongoIterable<String> collectionNames = database.listCollectionNames();
        List<MongoBucket> gridFSList = new ArrayList<>();
        for (String collectionName : collectionNames) {
            if (!collectionName.endsWith(".files")) {
                continue;
            }
            MongoBucket gridFS = new MongoBucket();
            gridFS.setDbName(dbName);
            gridFS.setName(collectionName.substring(0, collectionName.lastIndexOf(".")));
            gridFSList.add(gridFS);
        }
        return gridFSList;
    }

    public GridFSBucket createBucket(String dbName, String bucketName) {
        com.mongodb.client.MongoDatabase database = this.mongoClient.getDatabase(dbName);
        return GridFSBuckets.create(database, bucketName);
    }

    public void dropBucket(String dbName, String bucketName) {
        this.dropCollection(dbName, bucketName + ".files");
    }

    public void clearBucket(String dbName, String bucketName) {
        GridFSBucket bucket = this.bucket(dbName, bucketName);
        bucket.find().forEach(file -> bucket.delete(file.getObjectId()));
    }

    public List<MongoRecord> selectBucketRecords(MysqlSelectRecordParam param) {
        String dbName = param.getDbName();
        String collectionName = param.getCollectionName();
        GridFSBucket fsBucket = this.createBucket(dbName, collectionName);
        int skip = Math.toIntExact(param.getStart());
        int limit = Math.toIntExact(param.getLimit());

        Bson filters = MysqlConditionUtil.buildCondition(param.getFilters());
        GridFSFindIterable iterable = fsBucket.find(filters).limit(limit).skip(skip);
        List<MongoRecord> records = new ArrayList<>();
        MongoColumns columns = new MongoColumns();
        MongoColumn idColumn = new MongoColumn("ID");
        columns.add(idColumn);
        MongoColumn fileNameColumn = new MongoColumn(I18nHelper.fileName());
        columns.add(fileNameColumn);
        MongoColumn lengthColumn = new MongoColumn(I18nHelper.length());
        columns.add(lengthColumn);
        MongoColumn chunkSizeColumn = new MongoColumn(I18nHelper.chunkSize());
        columns.add(chunkSizeColumn);
        MongoColumn uploadDateColumn = new MongoColumn(I18nHelper.uploadDate());
        columns.add(uploadDateColumn);
        for (GridFSFile file : iterable) {
            MongoRecord record = new MongoRecord(columns, true);
            record.putValue(idColumn, file.getObjectId().toHexString());
            record.putValue(fileNameColumn, file.getFilename());
            record.putValue(lengthColumn, NumberUtil.formatSize(file.getLength()));
            record.putValue(chunkSizeColumn, NumberUtil.formatSize(file.getChunkSize()));
            record.putValue(uploadDateColumn, DateHelper.formatDate(file.getUploadDate()));
            records.add(record);
        }
        return records;
    }
}
