package cn.oyzh.easymongo.shell;

import com.mongodb.client.FindIterable;
import org.bson.Document;

public class ShellFindCursor extends ShellCursor {

    private final FindIterable<Document> cursor;

    public ShellFindCursor(FindIterable<Document> cursor) {
        super(cursor);
        this.cursor = cursor;
    }

    public ShellFindCursor limit(int n) {
        return new ShellFindCursor(cursor.limit(n));
    }

    public ShellFindCursor skip(int n) {
        return new ShellFindCursor(cursor.skip(n));
    }
}