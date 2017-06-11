package com.xjeffrose.nsfs;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
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
  static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);

  public FunctionGenerator() {

  }

  public static String generateClass(String functionBody) {

    MethodSpec main = MethodSpec.methodBuilder("main")
      .addModifiers(PUBLIC, STATIC)
      .returns(void.class)
      .addParameter(String[].class, "args")
      .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
      .build();

    TypeSpec nsfsGeneratedFunction = TypeSpec.classBuilder("NSFSGeneratedFunction")
      .addModifiers(PUBLIC, FINAL)
      .addMethod(main)
      .build();

    return "foo";
  }


}
