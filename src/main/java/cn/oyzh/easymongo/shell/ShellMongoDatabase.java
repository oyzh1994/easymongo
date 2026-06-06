package cn.oyzh.easymongo.shell;

import com.mongodb.client.ListCollectionNamesIterable;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.openjdk.nashorn.api.scripting.AbstractJSObject;

public class ShellMongoDatabase extends AbstractJSObject {

    private final MongoDatabase database;

    public ShellMongoDatabase(MongoDatabase database) {
        this.database = database;
    }

    public String getName() {
        return this.database.getName();
    }

    public ShellMongoCollection getCollection(String name) {
        MongoCollection<Document> collection = this.database.getCollection(name);
        return new ShellMongoCollection(collection);
    }

    public ShellCursor listCollectionNames() {
        ListCollectionNamesIterable iter = this.database.listCollectionNames();
        return new ShellCursor(iter);
    }

    public ShellCursor listCollections() {
        ListCollectionsIterable<Document> iter = this.database.listCollections();
        return new ShellCursor(iter);
    }

    @Override
    public Object getMember(String name) {
        return this.getCollection(name);
    }
}
