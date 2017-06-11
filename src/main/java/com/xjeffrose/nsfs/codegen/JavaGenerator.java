package com.xjeffrose.nsfs.codegen;

import com.squareup.javapoet.JavaFile;
import com.xjeffrose.nsfs.compiler.InMemoryCompiler;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.lang.reflect.Method;
import java.util.function.Function;
import okio.ByteString;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class JavaGenerator {

  private static final String entryPointName = "getFunction";
  private static final TypeName functionSpec = ParameterizedTypeName.get(Function.class, HttpRequest.class, HttpResponse.class);

  private static final ClassName BYTE_BUF = ClassName.get(ByteBuf.class);
  private static final ClassName BYTE_STRING = ClassName.get(ByteString.class);
  private static final ClassName UNPOOLED = ClassName.get(Unpooled.class);

  private static final ClassName FUNCTION = ClassName.get(Function.class);
  private static final ClassName HTTP_VERSION = ClassName.get(HttpVersion.class);
  private static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  private static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);
  private static final ClassName FULL_HTTP_RESPONSE = ClassName.get(FullHttpResponse.class);
  private static final ClassName HTTP_RESPONSE_STATUS = ClassName.get(HttpResponseStatus.class);
  private static final ClassName DEFAULT_FULL_HTTP_RESPONSE = ClassName.get(DefaultFullHttpResponse.class);

  // Create our sandbox of allowed imports
  private static final FieldSpec bb = FieldSpec.builder(BYTE_BUF, "bb").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec byte_string = FieldSpec.builder(BYTE_STRING, "byte_string").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec unpooled = FieldSpec.builder(UNPOOLED, "unpooled").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec http_version = FieldSpec.builder(HTTP_VERSION, "http_version").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec full_http_response = FieldSpec.builder(FULL_HTTP_RESPONSE, "full_http_response").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec http_response_status = FieldSpec.builder(HTTP_RESPONSE_STATUS, "http_response_status").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec deafult_full_http_response = FieldSpec.builder(DEFAULT_FULL_HTTP_RESPONSE, "default_full_http_response").addModifiers(PRIVATE, STATIC).build();
  private static final FieldSpec functionToRegister = FieldSpec.builder(functionSpec, "functionToRegister").addModifiers(PRIVATE).build();


  public JavaGenerator() {

  }

  public static Function<HttpRequest, HttpResponse> generateClass(String className, String functionBody) throws Exception {

    MethodSpec functionEntryPoint = MethodSpec.methodBuilder(entryPointName)
      .addModifiers(PUBLIC, FINAL)
      .returns(functionSpec)
      .addStatement(functionBody)
      .addStatement("return functionToRegister")
      .build();

    TypeSpec nsfsGeneratedClass = TypeSpec.classBuilder(className)
      .addModifiers(PUBLIC, FINAL)
      .addField(bb)
      .addField(byte_string)
      .addField(http_version)
      .addField(full_http_response)
      .addField(http_response_status)
      .addField(deafult_full_http_response)
      .addField(unpooled)
      .addField(functionToRegister)
      .addMethod(functionEntryPoint)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedClass)
      .addStaticImport(io.netty.handler.codec.http.HttpHeaderNames.class, "*")
      .build();

    // For Debugging - Keep Commented OUT
    System.out.println(javaFile.toString());

    Class<?> codeGenClass = InMemoryCompiler.compile("com.xjeffrose.nsfs." + className, javaFile.toString());
    Object o = codeGenClass.newInstance();
    Method m = codeGenClass.getDeclaredMethod(entryPointName);

    Object x = m.invoke(o);

    if (x instanceof Function) {
      return (Function<HttpRequest, HttpResponse>) x;
    } else {
      // We need to make a real exception
      throw new Exception();
    }
  }


}
