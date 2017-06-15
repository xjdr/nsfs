package com.xjeffrose.nsfs;

import org.junit.Test;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static com.xjeffrose.nsfs.http.Recipes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {

  @Test
  public void testAddFunctionHappyPath() throws Exception {

    Server server = new Server();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Server.HostedFunction> adapter = moshi.adapter(Server.HostedFunction.class);

    String payload = adapter.toJson(new Server.HostedFunction("/newFunction", "r -> newResponseOk()"));
    HttpRequest request = newRequestPost("/", payload, ContentType.Application_Json);
    HttpResponse response = server.addFunction.apply(request);

    assertEquals(HttpResponseStatus.OK, response.status());
  }

  @Test
  public void testAddFunctionBadRouteNoSlash() throws Exception {

    Server server = new Server();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Server.HostedFunction> adapter = moshi.adapter(Server.HostedFunction.class);

    String payload = adapter.toJson(new Server.HostedFunction("newFunction", "r -> newResponseOk()"));
    HttpRequest request = newRequestPost("/", payload, ContentType.Application_Json);
    HttpResponse response = server.addFunction.apply(request);

    assertEquals(HttpResponseStatus.BAD_REQUEST, response.status());
  }

  @Test
  public void testAddFunctionBadRouteDoubleSlash() throws Exception {

    Server server = new Server();
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Server.HostedFunction> adapter = moshi.adapter(Server.HostedFunction.class);

    String payload = adapter.toJson(new Server.HostedFunction("//newFunction", "r -> newResponseOk()"));
    HttpRequest request = newRequestPost("/", payload, ContentType.Application_Json);
    HttpResponse response = server.addFunction.apply(request);

    assertEquals(HttpResponseStatus.BAD_REQUEST, response.status());
  }

}
