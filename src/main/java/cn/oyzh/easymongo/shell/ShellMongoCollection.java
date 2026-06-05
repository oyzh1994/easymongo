package cn.oyzh.easymongo.shell;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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

    public void insertOne(Object doc) {
        if (doc instanceof Map map) {
            collection.insertOne(new Document(map));
        }
    }
}
