package com.xjeffrose.nsfs;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.xjeffrose.nsfs.codegen.JavaGenerator;
import com.xjeffrose.nsfs.http.Router;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okio.ByteString;


@Slf4j
public class Server implements Runnable {

  private final Router router = new Router();
  private final Moshi moshi = new Moshi.Builder().build();
  private final Type type = Types.getRawType(HostedFunction.class);
  private final JsonAdapter<HostedFunction> adapter = moshi.adapter(type);

  private final Function<HttpRequest, HttpResponse> healthCheck = x -> {

    // TODO(JR): Need to do more stuffs in here

    String respMsg = "OK";

    FullHttpResponse response = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.OK,
      Unpooled.copiedBuffer(respMsg.getBytes()));

    response.headers().set(CONTENT_TYPE, "text/plain");
    response.headers().setInt(CONTENT_LENGTH, respMsg.length());

    return response;
  };

  private final Function<HttpRequest, HttpResponse> addFunction = x -> {
    try {

      HostedFunction f = adapter.fromJson(ByteString.of(((FullHttpRequest) x).content().nioBuffer()).utf8());

      // TODO(JR): We need a better naming convention
      router.addRoute(f.route, JavaGenerator.generateClass("hello" , f.functionBody));

    } catch (Exception e) {
      // TODO(JR): We should prob return the compiler errors in the error response body
      log.error("Error Registering Function", (Throwable) e);

      String errorString = e.toString();

      HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(errorString.getBytes()));
      response.headers().set(CONTENT_TYPE, "text/plain");
      response.headers().setInt(CONTENT_LENGTH, errorString.length());

      return response;
    }

    String respMsg = "OK";

    // TODO(JR): We need a more reasonable return body with more useful info
    FullHttpResponse response = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.OK,
      Unpooled.copiedBuffer(respMsg.getBytes())
    );

    response.headers().set(CONTENT_TYPE, "text/plain");
    response.headers().setInt(CONTENT_LENGTH, respMsg.length());

    return response;
  };

  public Server() {

  }

  public void start() {

    router.addRoute("/healthCheck", healthCheck); // Add Health Check
    router.addRoute("/addFunction", addFunction); // Add a function

    try {
      // Fire away
      router.listenAndServe();
    } catch (IOException e) {
      log.error("Failed to start people server", e);
    }

  }

  @Override
  public void run() {
    start();
  }

  public void close() {
    router.shutdown();
  }

  @AllArgsConstructor
  private static class HostedFunction {

    private final String route;
    private final String functionBody;

  }


}
