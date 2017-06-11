package com.xjeffrose.nsfs.codegen;

import static org.junit.Assert.*;

import com.squareup.javapoet.JavaFile;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.junit.Test;

public class JavaGeneratorTest {

  @Test
  public void generateClass() throws Exception {
    Function<HttpRequest, HttpResponse> functionToRegister = JavaGenerator
      .generateClass("xx");

    HttpResponse resp = functionToRegister.apply(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"));

    assertEquals(HttpResponseStatus.OK, resp.status());
  }
}
