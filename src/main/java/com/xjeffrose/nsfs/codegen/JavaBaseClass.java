package com.xjeffrose.nsfs.codegen;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import okio.ByteString;

@Slf4j
public abstract class JavaBaseClass implements GeneratedFunction {

  protected ByteBuf bb;

  protected ByteString byte_string;

  protected Unpooled unpooled;

  protected HttpVersion http_version;

  protected FullHttpResponse full_http_response;

  protected HttpResponseStatus http_response_status;

  protected DefaultFullHttpResponse default_full_http_response;

  public final GeneratedFunction getGeneratedFunction() {
    return this;
  }

  public final Function<HttpRequest, HttpResponse> getFunction() {
    return this::onRequest;
  }

  public abstract HttpResponse onRequest(HttpRequest request);

}
