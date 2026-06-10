package cn.oyzh.easymongo.shell;

import cn.oyzh.common.util.ReflectUtil;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShellMongoCollection {

    private final String dbName;

    private final String collectionName;

    private final MongoCollection<Document> collection;

    public ShellMongoCollection(String dbName, String collectionName, MongoCollection<Document> collection) {
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.collection = collection;
    }

    public ShellFindCursor find() {
        return this.find(null);
    }

    public ShellFindCursor find(Object doc) {
        Document filter;
        if (doc instanceof Map map) {
            filter = new Document(map);
        } else {
            filter = new Document();
        }
        FindIterable<Document> iter = this.collection.find(filter);
        return new ShellFindCursor(this.dbName, this.collectionName, iter);
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


}
