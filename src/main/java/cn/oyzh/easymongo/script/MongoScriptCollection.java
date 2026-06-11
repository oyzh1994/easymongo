package cn.oyzh.easymongo.script;

import cn.oyzh.common.json.JSONUtil;
import com.mongodb.MongoNamespace;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MongoScriptCollection {

    private final String dbName;

    private final String collectionName;

    private final MongoCollection<Document> collection;

    public MongoScriptCollection(MongoCollection<Document> collection) {
        this.dbName = collection.getNamespace().getDatabaseName();
        this.collectionName = collection.getNamespace().getCollectionName();
        this.collection = collection;
    }

    public MongoScriptFindCursor find() {
        return this.find(null);
    }

    public MongoScriptFindCursor find(Object doc) {
        Document filter;
        if (doc instanceof Map map) {
            filter = new Document(map);
        } else {
            filter = new Document();
        }
        FindIterable<Document> iter = this.collection.find(filter);
        return new MongoScriptFindCursor(this.dbName, this.collectionName, iter);
    }

    public InsertOneResult insert(Object doc) {
        return this.insertOne(doc);
    }

    public InsertOneResult insertOne(Object doc) {
        if (doc instanceof Map map) {
            return this.collection.insertOne(new Document(map));
        }
        return null;
    }

    public InsertManyResult insertMany(Object doc) {
        if (doc instanceof ScriptObjectMirror mirror) {
            return this.insertMany(mirror.values());
        }
        if (doc instanceof Collection collection) {
            List<Document> list = new ArrayList<>();
            for (Object o : collection) {
                if (o instanceof Map map) {
                    list.add(new Document(map));
                }
            }
            return this.collection.insertMany(list);
        }
        return null;
    }

    public DeleteResult delete(Object doc) {
        return this.deleteOne(doc);
    }

    public DeleteResult deleteOne(Object doc) {
        if (doc instanceof Map map) {
            return this.collection.deleteOne(new Document(map));
        }
        return null;
    }

    public DeleteResult deleteMany() {
        return this.deleteMany(null);
    }

    public DeleteResult deleteMany(Object doc) {
        Document filter;
        if (doc instanceof Map map) {
            filter = new Document(map);
        } else {
            filter = new Document();
        }
        return this.collection.deleteMany(filter);
    }

    public UpdateResult update(Object filter, Object doc) {
        return this.updateOne(filter, doc);
    }

    public UpdateResult updateOne(Object filter, Object doc) {
        if (filter instanceof Map f && doc instanceof Map map) {
            return this.collection.updateOne(new Document(f), new Document(map));
        }
        return null;
    }

    public void drop() {
        this.collection.drop();
    }

    // --- findOne ---

    public Document findOne() {
        return this.collection.find().first();
    }

    public Document findOne(Object filter) {
        if (filter instanceof Map map) {
            return this.collection.find(new Document(map)).first();
        }
        return this.collection.find().first();
    }

    // --- findOneAndDelete ---

    public Document findOneAndDelete(Object filter) {
        if (filter instanceof Map map) {
            return this.collection.findOneAndDelete(new Document(map));
        }
        return null;
    }

    // --- findOneAndReplace ---

    public Document findOneAndReplace(Object filter, Object replacement) {
        if (filter instanceof Map f && replacement instanceof Map r) {
            return this.collection.findOneAndReplace(new Document(f), new Document(r));
        }
        return null;
    }

    // --- findOneAndUpdate ---

    public Document findOneAndUpdate(Object filter, Object update) {
        if (filter instanceof Map f && update instanceof Map u) {
            return this.collection.findOneAndUpdate(new Document(f), new Document(u));
        }
        return null;
    }

    // --- updateMany ---

    public UpdateResult updateMany(Object filter, Object update) {
        if (filter instanceof Map f && update instanceof Map u) {
            return this.collection.updateMany(new Document(f), new Document(u));
        }
        return null;
    }

    // --- replaceOne ---

    public UpdateResult replaceOne(Object filter, Object replacement) {
        if (filter instanceof Map f && replacement instanceof Map r) {
            return this.collection.replaceOne(new Document(f), new Document(r));
        }
        return null;
    }

    public UpdateResult replaceOne(Object filter, Object replacement, Object option) {
        if (filter instanceof Map f && replacement instanceof Map r && option instanceof Map<?, ?> o) {
            ReplaceOptions options = JSONUtil.toBean(o, ReplaceOptions.class);
            return this.collection.replaceOne(new Document(f), new Document(r), options);
        }
        return null;
    }

    // --- countDocuments ---

    public long countDocuments() {
        return this.collection.countDocuments();
    }

    public long countDocuments(Object filter) {
        if (filter instanceof Map map) {
            return this.collection.countDocuments(new Document(map));
        }
        return this.collection.countDocuments();
    }

    // --- estimatedDocumentCount ---

    public long estimatedDocumentCount() {
        return this.collection.estimatedDocumentCount();
    }

    // --- distinct ---

    public MongoScriptCursor distinct(String fieldName) {
        return new MongoScriptCursor(this.collection.distinct(fieldName, String.class));
    }

    public MongoScriptCursor distinct(String fieldName, Object filter) {
        if (filter instanceof Map map) {
            return new MongoScriptCursor(this.collection.distinct(fieldName, new Document(map), String.class));
        }
        return new MongoScriptCursor(this.collection.distinct(fieldName, String.class));
    }

    // --- aggregate ---

    public MongoScriptCursor aggregate(Object pipeline) {
        List<Document> stages = MongoScriptUtil.toDocumentList(pipeline);
        AggregateIterable<Document> iter = this.collection.aggregate(stages);
        iter.allowDiskUse(true);
        return new MongoScriptCursor(iter);
    }

    // --- indexes ---

    public String createIndex(Object keys) {
        return this.createIndex(keys, null);
    }

    public String createIndex(Object keys, Object options) {
        if (!(keys instanceof Map k)) {
            return null;
        }
        IndexOptions opts = new IndexOptions();
        if (options instanceof Map optMap) {
            if (optMap.containsKey("name")) {
                opts.name(optMap.get("name").toString());
            }
            if (optMap.containsKey("unique")) {
                opts.unique(Boolean.parseBoolean(optMap.get("unique").toString()));
            }
            if (optMap.containsKey("background")) {
                opts.background(Boolean.parseBoolean(optMap.get("background").toString()));
            }
            if (optMap.containsKey("sparse")) {
                opts.sparse(Boolean.parseBoolean(optMap.get("sparse").toString()));
            }
            if (optMap.containsKey("expireAfterSeconds")) {
                opts.expireAfter(Long.parseLong(optMap.get("expireAfterSeconds").toString()), TimeUnit.SECONDS);
            }
        }
        return this.collection.createIndex(new Document(k), opts);
    }

    public MongoScriptCursor listIndexes() {
        return new MongoScriptCursor(this.collection.listIndexes());
    }

    public void dropIndex(Object keys) {
        if (keys instanceof Map map) {
            this.collection.dropIndex(new Document(map));
        }
    }

    public void dropIndexByName(String name) {
        this.collection.dropIndex(name);
    }

    public void dropIndexes() {
        this.collection.dropIndexes();
    }

    // --- rename ---

    public void rename(String newName) {
        this.collection.renameCollection(new MongoNamespace(this.dbName, newName));
    }
}
