package com.xjeffrose.nsfs;

import com.squareup.javapoet.JavaFile;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
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

public class FunctionGenerator {

  static final ClassName BYTE_STRING = ClassName.get(ByteString.class);
  static final ClassName BYTE_BUF = ClassName.get(ByteBuf.class);

  static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);

  public FunctionGenerator() {

  }

  public static String generateClass(String functionBody) {
    UUID functionID = UUID.randomUUID();

    MethodSpec functionEntryPoint = MethodSpec.methodBuilder("Hello")
      .addModifiers(PUBLIC)
      .returns(HttpResponse.class)
      .addStatement("return $S", "hello again")
      .build();

    TypeSpec nsfsGeneratedFunction = TypeSpec.classBuilder(functionID.toString())
      .addModifiers(PUBLIC, FINAL)
      .addMethod(functionEntryPoint)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedFunction)
      .build();

    // need to return uuid & class body
    return javaFile.toString();
  }


}
