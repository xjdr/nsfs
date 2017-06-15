package com.xjeffrose.nsfs;

import static com.xjeffrose.nsfs.http.Recipes.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.xjeffrose.nsfs.codegen.JavaGenerator;
import com.xjeffrose.nsfs.http.Router;
import com.xjeffrose.nsfs.http.RouteValidator;
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

  final Function<HttpRequest, HttpResponse> addFunction = x -> {
    try {

      HostedFunction f = adapter.fromJson(ByteString.of(((FullHttpRequest) x).content().nioBuffer()).utf8());

      // Validate Route
      if (!RouteValidator.isValid(f.route)) {
        return newResponseBadRequest("Invalid Route: " + f.route);
      }
      // TODO(CK): valid routes should be mangled into a generated class name
      String route = "/functions" + f.route;
      // TODO(JR): We need a better naming convention
      String className = "hello";
      // TODO(CK): generateClass should return an Optional, we know that compilation may fail
      Function<HttpRequest, HttpResponse> newFunction = JavaGenerator.generateClass(className, f.functionBody);
      // TODO(CK): check for collision with existing routes
      router.addRoute(route, newFunction);

      GeneratedFunction generatedFunction = new GeneratedFunction(route, "stateless", className);

      String payload = moshi.adapter(GeneratedFunction.class).toJson(generatedFunction);

      return newResponseOk(payload, ContentType.Application_Json);
    } catch (Exception e) {
      // TODO(JR): We should prob return the compiler errors in the error response body
      log.error("Error Registering Function", (Throwable) e);
      String errorString = e.toString();

      return newResponseBadRequest(errorString);
    }
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
  static class HostedFunction {

    private final String route;
    private final String functionBody;

  }

  @AllArgsConstructor
  static class GeneratedFunction {
    final String route;
    final String type;
    final String className;
  }

}
