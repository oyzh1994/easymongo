package cn.oyzh.easymongo.shell;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.Map;

public class ShellMongoCollection {

    private final MongoCollection<Document> collection;

    public ShellMongoCollection(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public ShellFindCursor find() {
        return this.find(null);
    }

    public ShellFindCursor find(Object query) {
        Document filter;
        if (query instanceof Map map) {
            filter = new Document(map);
        } else {
            filter = new Document();
        }
        FindIterable<Document> iter = this.collection.find(filter);
        return new ShellFindCursor(iter);
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

    public DeleteResult delete(Object doc) {
        return this.deleteOne(doc);
    }

    public DeleteResult deleteOne(Object doc) {
        if (doc instanceof Map map) {
            return this.collection.deleteOne(new Document(map));
        }
        return null;
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
