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
import org.codehaus.groovy.runtime.powerassert.SourceText;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class JavaGenerator {

  private static final String className = "Hello";
  private static final String entryPointName = "getFunction";
  private static final TypeName functionSpec = ParameterizedTypeName.get(Function.class, HttpRequest.class, HttpResponse.class);

  private static final ClassName BYTE_STRING = ClassName.get(ByteString.class);
  private static final ClassName BYTE_BUF = ClassName.get(ByteBuf.class);
  private static final ClassName UNPOOLED = ClassName.get(Unpooled.class);

  private static final ClassName FUNCTION = ClassName.get(Function.class);
  private static final ClassName HTTP_VERSION = ClassName.get(HttpVersion.class);
  private static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  private static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);
  private static final ClassName FULL_HTTP_RESPONSE = ClassName.get(FullHttpResponse.class);
  private static final ClassName HTTP_RESPONSE_STATUS = ClassName.get(HttpResponseStatus.class);
  private static final ClassName DEFAULT_FULL_HTTP_RESPONSE = ClassName.get(DefaultFullHttpResponse.class);

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

  static final FieldSpec functionToRegister = FieldSpec.builder(functionSpec, "functionToRegister").build();


  public JavaGenerator() {

  }

  public static Function<HttpRequest, HttpResponse> generateClass(String functionBody) throws Exception {
    //UUID functionID = UUID.randomUUID();

    MethodSpec functionEntryPoint = MethodSpec.methodBuilder(entryPointName)
      .addModifiers(PUBLIC)
      .returns(functionSpec)
      .addStatement("functionToRegister = x -> { "
        + "return new $T("
        + "$T.HTTP_1_1,"
        + "$T.OK); };",
        DEFAULT_FULL_HTTP_RESPONSE,
        HTTP_VERSION,
        HTTP_RESPONSE_STATUS)
      .addStatement("return functionToRegister")
      .build();

    TypeSpec nsfsGeneratedClass = TypeSpec.classBuilder(className)
      .addModifiers(PUBLIC, FINAL)
      .addMethod(functionEntryPoint)
      .addField(functionToRegister)
      .addField(okResponse)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedClass)
      .build();

    Class<?> codeGenClass = InMemoryCompiler.compile("com.xjeffrose.nsfs." + className, javaFile.toString());
    Object o = codeGenClass.newInstance();
    Method m = codeGenClass.getDeclaredMethod(entryPointName);

    Function<HttpRequest, HttpResponse> functionToRegister = (Function<HttpRequest, HttpResponse>) m.invoke(o);

    return functionToRegister;
  }


}
