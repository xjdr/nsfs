package com.xjeffrose.nsfs.codegen;

import com.squareup.javapoet.JavaFile;
import com.xjeffrose.nsfs.compiler.InMemoryCompiler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@Slf4j
public class JavaGenerator {

  private static final TypeName functionSpec = ParameterizedTypeName.get(Function.class, HttpRequest.class, HttpResponse.class);
  private static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  private static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);

  public JavaGenerator() {
  }

  public static Function<HttpRequest, HttpResponse> generateClass(String className, String functionBody, Class<? extends JavaBaseClass> baseClass) throws Exception {

    MethodSpec getFunction = MethodSpec.methodBuilder("getFunction")
      .addModifiers(PUBLIC, FINAL)
      .addCode(CodeBlock.builder().add("onRequest = " + functionBody + ";").build())
      .addStatement("return onRequest")
      .returns(functionSpec)
      .build();

    TypeSpec nsfsGeneratedClass = TypeSpec.classBuilder(className)
      .addModifiers(PUBLIC, FINAL)
      .addMethod(getFunction)
      .superclass(baseClass)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedClass)
      .addStaticImport(io.netty.handler.codec.http.HttpHeaderNames.class, "*")
      .addStaticImport(com.xjeffrose.nsfs.http.Recipes.class, "*")
      .build();

    JavaImportInjector importInjector = new JavaImportInjector();
    importInjector.addImport("io.netty.handler.codec.http.*");
    importInjector.addImport("io.netty.buffer.*");
    importInjector.addImport("java.sql.*");
    javaFile.writeTo(importInjector);
    String source = importInjector.toString();

    // For Debugging - Keep Commented OUT
    // System.out.println(source);

    Class<?> codeGenClass = InMemoryCompiler.compile("com.xjeffrose.nsfs." + className, source);
    Object o = codeGenClass.newInstance();
    if (o instanceof JavaBaseClass) {
      JavaBaseClass base = (JavaBaseClass)o;
      Function<HttpRequest, HttpResponse> func = base.getFunction();
      return func;
    } else {
      // We need to make a real exception
      throw new Exception();
    }
  }

  public static Function<HttpRequest, HttpResponse> generateClass(String className, String functionBody) throws Exception {
    return generateClass(className, functionBody, JavaBaseClass.class);
  }

}
