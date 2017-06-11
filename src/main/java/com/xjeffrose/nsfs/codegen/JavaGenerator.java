package com.xjeffrose.nsfs.codegen;

import com.google.common.collect.ImmutableMap;
import com.squareup.javapoet.JavaFile;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import okio.ByteString;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.NameAllocator;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class JavaGenerator {

  static final ClassName BYTE_STRING = ClassName.get(ByteString.class);
  static final ClassName BYTE_BUF = ClassName.get(ByteBuf.class);
  static final ClassName UNPOOLED = ClassName.get(Unpooled.class);

  static final ClassName FUNCTION = ClassName.get(Function.class);
  static final ClassName HTTP_VERSION = ClassName.get(HttpVersion.class);
  static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);
  static final ClassName FULL_HTTP_RESPONSE = ClassName.get(FullHttpResponse.class);
  static final ClassName HTTP_RESPONSE_STATUS = ClassName.get(HttpResponseStatus.class);
  static final ClassName DEFAULT_FULL_HTTP_RESPONSE = ClassName.get(DefaultFullHttpResponse.class);

  static final FieldSpec okResponse = FieldSpec.builder(HttpResponse.class, "defaultOkResponse")
    .addModifiers(PRIVATE, FINAL)
    .initializer("new $T(\n"
        + "      $T.HTTP_1_1,\n"
        + "      $T.OK,\n"
        + "      $T.copiedBuffer(new String(\"ok\").getBytes()));",
      DEFAULT_FULL_HTTP_RESPONSE,
      HTTP_VERSION,
      HTTP_RESPONSE_STATUS,
      UNPOOLED)
    .build();

  public JavaGenerator() {

  }

  public static Map<String, JavaFile> generateClass(String functionBody) {
    //UUID functionID = UUID.randomUUID();

    MethodSpec functionEntryPoint = MethodSpec.methodBuilder("Hello")
      .addModifiers(PUBLIC)
      .returns(HttpResponse.class)
      .addStatement("return defaultOkResponse")
      .build();

    TypeSpec nsfsGeneratedFunction = TypeSpec.classBuilder("Hello")
      .addModifiers(PUBLIC, FINAL)
      .addField(okResponse)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedFunction)
      .build();

    return ImmutableMap.of("Hello", javaFile);
  }


}
