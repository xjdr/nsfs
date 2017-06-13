package com.xjeffrose.nsfs.codegen;

import static org.junit.Assert.assertEquals;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.function.Function;
import org.junit.Test;

public class JavaGeneratorTest {

  @Test
  public void generateClass() throws Exception {
    HttpRequest testReq = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
    String functionBody = ""
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
      + "    return response;";

    Function<HttpRequest, HttpResponse> functionToRegister = JavaGenerator
      .generateClass("TestClassName", functionBody);

    HttpResponse resp = functionToRegister.apply(testReq);

    assertEquals(HttpResponseStatus.OK, resp.status());
  }
}
