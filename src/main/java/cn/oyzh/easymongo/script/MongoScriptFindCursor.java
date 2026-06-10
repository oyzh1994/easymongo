package cn.oyzh.easymongo.script;

import com.mongodb.client.FindIterable;
import org.bson.Document;

public class MongoScriptFindCursor extends MongoScriptCursor {

    private final FindIterable<Document> cursor;
    private final String dbName;
    private final String collectionName;

    public MongoScriptFindCursor(String dbName, String collectionName, FindIterable<Document> cursor) {
        super(cursor);
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.cursor = cursor;
    }

    public MongoScriptFindCursor limit(int n) {
        return new MongoScriptFindCursor(this.dbName, this.collectionName, this.cursor.limit(n));
    }

    public MongoScriptFindCursor skip(int n) {
        return new MongoScriptFindCursor(this.dbName, this.collectionName, this.cursor.skip(n));
    }

    public Document explain() {
       return this.cursor.explain();
    }

//    @Override
//    public List<MongoRecord> toArray() {
//        return MongoRecordUtil.docToRecord(this.dbName, this.collectionName, this.cursor);
//    }
}