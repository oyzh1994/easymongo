package cn.oyzh.easymongo.shell;

import cn.oyzh.easymongo.mongo.MongoRecord;
import cn.oyzh.easymongo.util.MongoRecordUtil;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.List;

public class ShellFindCursor extends ShellCursor {

    private final FindIterable<Document> cursor;
    private final String dbName;
    private final String collectionName;

    public ShellFindCursor(String dbName, String collectionName, FindIterable<Document> cursor) {
        super(cursor);
        this.dbName = dbName;
        this.collectionName = collectionName;
        this.cursor = cursor;
    }

    public ShellFindCursor limit(int n) {
        return new ShellFindCursor(this.dbName, this.collectionName, this.cursor.limit(n));
    }

    public ShellFindCursor skip(int n) {
        return new ShellFindCursor(this.dbName, this.collectionName, this.cursor.skip(n));
    }

    public Document explain() {
       return this.cursor.explain();
    }

//    @Override
//    public List<MongoRecord> toArray() {
//        return MongoRecordUtil.docToRecord(this.dbName, this.collectionName, this.cursor);
//    }
}