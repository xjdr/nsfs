package com.xjeffrose.nsfs;

import static com.xjeffrose.nsfs.http.Recipes.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.xjeffrose.nsfs.codegen.JavaGenerator;
import com.xjeffrose.nsfs.http.Router;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
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

    return newResponseOk(respMsg);
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

      return newResponseBadRequest(errorString);
    }

    String respMsg = "OK";

    // TODO(JR): We need a more reasonable return body with more useful info
    return newResponseOk(respMsg);
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
