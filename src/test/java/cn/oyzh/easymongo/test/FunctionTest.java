package cn.oyzh.easymongo.test;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Code;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class FunctionTest {

    private com.mongodb.client.MongoClient mongoClient() {
        return MongoClients.create("mongodb://admin:123456@127.0.0.1:27017/admin");
    }

    @Test
    public void test1() {
        try (com.mongodb.client.MongoClient mongoClient = this.mongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> systemJs = db.getCollection("system.js");

            // 创建一个函数，用 Code 包装 JavaScript 代码
            String functionBody = "function(a, b) { return a + b; }";
            Document funcDoc = new Document()
                    .append("_id", "add")
                    .append("value", new Code(functionBody));

            // 如果已存在则替换
            systemJs.replaceOne(
                    new Document("_id", "add"),
                    funcDoc,
                    new com.mongodb.client.model.ReplaceOptions().upsert(true)
            );

            System.out.println("函数已保存");

            MongoCollection<Document> users = db.getCollection("test1");

            Bson whereFilter = Filters.where("add(this.a, 5) > 6");
            users.find(whereFilter).forEach(d->{
                System.out.println(d.toJson());
            });
        }
    }

    @Test
    public void test2() {
        try (com.mongodb.client.MongoClient mongoClient = this.mongoClient()) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> systemJs = db.getCollection("system.js");

            FindIterable<Document> iterable=  systemJs.find();

            for (Document document : iterable) {
                System.out.println(document.entrySet());
            }
        }
    }

}
