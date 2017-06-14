package com.xjeffrose.nsfs.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;

public final class Recipes {
  protected Recipes() {}

  private static final HttpVersion v1_1 = HttpVersion.HTTP_1_1;

  public static enum ContentType {
    Text_Plain("text/plain"),
    Text_Html("text/html");

    private final String value;

    ContentType(String value) {
      this.value = value;
    }

  }

  public static HttpResponse newResponse(HttpResponseStatus status) {
    HttpResponse response = new DefaultHttpResponse(v1_1, status);

    return response;
  }

  public static HttpResponse newResponse(HttpResponseStatus status, ByteBuf buffer, ContentType contentType) {
    FullHttpResponse response = new DefaultFullHttpResponse(v1_1, status, buffer);

    response.headers().set(CONTENT_TYPE, contentType.value);
    response.headers().setInt(CONTENT_LENGTH, buffer.readableBytes());

    return response;
  }

  public static HttpResponse newResponse(HttpResponseStatus status, String payload, ContentType contentType) {
    return newResponse(status,
                       Unpooled.copiedBuffer(payload.getBytes(StandardCharsets.UTF_8)),
                       contentType);
  }

  // OK {{{
  public static HttpResponse newResponseOk() {
    return newResponse(HttpResponseStatus.OK);
  }

  public static HttpResponse newResponseOk(String payload) {
    return newResponse(HttpResponseStatus.OK, payload, ContentType.Text_Plain);
  }

  public static HttpResponse newResponseOk(String payload, ContentType contentType) {
    return newResponse(HttpResponseStatus.OK, payload, contentType);
  }
  // OK }}}

  // BAD_REQUEST {{{
  public static HttpResponse newResponseBadRequest() {
    return newResponse(HttpResponseStatus.BAD_REQUEST);
  }

  public static HttpResponse newResponseBadRequest(String payload) {
    return newResponse(HttpResponseStatus.BAD_REQUEST, payload, ContentType.Text_Plain);
  }

  public static HttpResponse newResponseBadRequest(String payload, ContentType contentType) {
    return newResponse(HttpResponseStatus.BAD_REQUEST, payload, contentType);
  }
  // BAD_REQUEST }}}
}
