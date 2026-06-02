package cn.oyzh.easymongo.test;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author oyzh
 * @since 2026-06-01
 */
public class MongoTest {

    @Test
    public void test1() throws ClassNotFoundException, SQLException {

        String model = """
                {
                  "version": "1.0",
                  "defaultSchema": "mongo",
                  "schemas": [
                    {
                      "name": "mongo",
                      "type": "custom",
                      "factory": "org.apache.calcite.adapter.mongodb.MongoSchemaFactory",
                      "operand": {
                        "host": "127.0.0.1:27017",
                        "database": "test",
                        "authMechanism": "SCRAM-SHA-1",
                        "username": "admin",
                        "password": "123456"
                      }
                    }
                  ]
                }
                """;

        // 1. 加载 Calcite JDBC 驱动
        Class.forName("org.apache.calcite.jdbc.Driver");

        // 2. 指定模型文件路径（或内联）
        Connection connection = DriverManager.getConnection(
                "jdbc:calcite:inline:" + model);

        // 3. 创建 Statement 并执行 SQL
        Statement stmt = connection.createStatement();
        String sql = "SELECT name, age FROM \"users\" WHERE age > 12";
        ResultSet rs = stmt.executeQuery(sql);

        // 4. 遍历结果
        while (rs.next()) {
            String name = rs.getString("name");
            int age = rs.getInt("age");
            System.out.println(name + " - " + age);
        }

        rs.close();
        stmt.close();
        connection.close();
    }

    @Test
    public void test2() throws ClassNotFoundException, SQLException {
        // 1. 加载 Calcite JDBC 驱动
        Class.forName("org.apache.calcite.jdbc.Driver");

        String modelPath = MongoTest.class.getResource("/model.json").getPath();

        // 2. 指定模型文件路径（或内联）
        Connection connection = DriverManager.getConnection(
                "jdbc:calcite:model=" + modelPath);

        // 3. 创建 Statement 并执行 SQL
        Statement stmt = connection.createStatement();
        String sql = "SELECT name, age FROM \"users\" WHERE age > 12";
        ResultSet rs = stmt.executeQuery(sql);

        // 4. 遍历结果
        while (rs.next()) {
            String name = rs.getString("name");
            int age = rs.getInt("age");
            System.out.println(name + " - " + age);
        }

        rs.close();
        stmt.close();
        connection.close();
    }

    @Test
    public void test3() {
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@127.0.0.1:27017/admin")) {
            MongoDatabase db = mongoClient.getDatabase("test");
            for (String name : db.listCollectionNames()) {
                System.out.println(name);
            }
        }
    }

    @Test
    public void test4() {
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@127.0.0.1:27017/admin")) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection("users");
            FindIterable<Document> iterable = collection.find();
            for (Document document : iterable) {
                System.out.println("name=" + document.get("name"));
                System.out.println("age=" + document.get("age"));
                System.out.println("----");
            }
        }
    }

    @Test
    public void test5() {
        try (com.mongodb.client.MongoClient mongoClient = MongoClients.create("mongodb://admin:123456@127.0.0.1:27017/admin")) {
            MongoDatabase db = mongoClient.getDatabase("test");
            MongoCollection<Document> collection = db.getCollection("users");
            Bson f1 = Filters.regex("name", "五$");
            Bson f2 = Filters.expr(
                    new Document("$regexMatch",
                            new Document("input", new Document("$toString", "$_id"))
                                    .append("regex", "be$")
                    )
            );
            Bson f3 = Filters.not(f1);
            FindIterable<Document> iterable = collection.find(f3);
            for (Document document : iterable) {
                System.out.println("name=" + document.get("name"));
                System.out.println("age=" + document.get("age"));
                System.out.println("----");
            }
        }
    }

}
