package com.xjeffrose.nsfs.codegen;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Joiner;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Test;

public class JavaGeneratorTest {

  @Test
  public void generateClass() throws Exception {
    HttpRequest testReq = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    String functionBody = "request -> {"
      +      "String respMsg = \"OK\";\n"
      + "\n"
      + "    FullHttpResponse response = new DefaultFullHttpResponse(\n"
      + "      HttpVersion.HTTP_1_1,\n"
      + "      HttpResponseStatus.OK,\n"
      + "      Unpooled.copiedBuffer(respMsg.getBytes()));\n"
      + "    \n"
      + "      response.headers().set(CONTENT_TYPE, \"text/plain\");\n"
      + "      response.headers().setInt(CONTENT_LENGTH, respMsg.length());\n"
      + "\n"
      + "    return response;"
      + "}";

    Function<HttpRequest, HttpResponse> functionToRegister = JavaGenerator
      .generateClass("TestClassName", functionBody);

    HttpResponse resp = functionToRegister.apply(testReq);

    assertEquals(HttpResponseStatus.OK, resp.status());
  }

  public static abstract class InMemoryBaseClass extends JavaJDBCBaseClass {
    private static Connection connection = null;
    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
      if (connection != null) {
        return connection;
      }
      String connectionURL = "jdbc:derby:memory:testDB;create=true";
      connection = DriverManager.getConnection(connectionURL);
      String DDL = Joiner.on("\n")
        .join(
              "CREATE TABLE test_table (",
              "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), ",
              "name VARCHAR(20) NOT NULL, ",
              "count INT NOT NULL, ",
              "PRIMARY KEY (id))"
              );
      Statement stmnt = connection.createStatement();
      stmnt.executeUpdate(DDL);
      return connection;
    }
  }

  @Test
  public void generateStatefulClass() throws Exception {
    HttpRequest testReq = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    String query = Joiner.on(" ")
      .join("SELECT * FROM test_table",
            "WHERE name = 'bob'"
            );
    String inserter = Joiner.on(" ")
      .join("INSERT INTO test_table",
            "(name, count) VALUES ('bob', 1)"
            );
    String updater = Joiner.on(" ")
      .join("UPDATE test_table SET",
            "count = count + 1",
            "WHERE name = 'bob'"
            );
    String functionBody = Joiner.on("\n")
      .join(
            "request -> {",
            "try {",
            Joiner.on("\n  ")
            .join(
                  "Connection connection = getConnection();",
                  "Statement statement = connection.createStatement();",
                  "try (ResultSet rs = statement.executeQuery(\"" + query + "\")) {",
                  "  if (rs.next()) {",
                  "    statement.executeUpdate(\"" + updater + "\");",
                  "  } else {",
                  "    statement.executeUpdate(\"" + inserter + "\");",
                  "  }",
                  "}",
                  "ResultSet rs = statement.executeQuery(\"" + query + "\");",
                  "",
                  "boolean r = rs.next();",
                  "int result = rs.getInt(\"count\");",
                  "",
                  "String respMsg = Integer.toString(result);",
                  "",
                  "FullHttpResponse response = new DefaultFullHttpResponse(",
                  "  HttpVersion.HTTP_1_1,",
                  "  HttpResponseStatus.OK,",
                  "  Unpooled.copiedBuffer(respMsg.getBytes()));",
                  "",
                  "response.headers().set(CONTENT_TYPE, \"text/plain\");",
                  "response.headers().setInt(CONTENT_LENGTH, respMsg.length());",
                  "",
                  "return response;"
                  ),
            "} catch (SQLException | ClassNotFoundException e) {",
            "e.printStackTrace();",
            "return new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);",
            "}",
            "}"
            );

    Function<HttpRequest, HttpResponse> functionToRegister = JavaGenerator
      .generateClass("TestClassName", functionBody, InMemoryBaseClass.class);

    HttpResponse resp = functionToRegister.apply(testReq);

    assertEquals(HttpResponseStatus.OK, resp.status());
    if (resp instanceof FullHttpResponse) {
      assertEquals("1", ((FullHttpResponse)resp).content().readCharSequence(1, StandardCharsets.UTF_8));
    } else {
      assertEquals(true, false);
    }

    resp = functionToRegister.apply(testReq);

    assertEquals(HttpResponseStatus.OK, resp.status());
    if (resp instanceof FullHttpResponse) {
      assertEquals("2", ((FullHttpResponse)resp).content().readCharSequence(1, StandardCharsets.UTF_8));
    } else {
      assertEquals(true, false);
    }
  }

}
