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
import com.squareup.javapoet.TypeSpec;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@Slf4j
public class JavaGenerator {

  private static final ClassName HTTP_REQUEST = ClassName.get(HttpRequest.class);
  private static final ClassName HTTP_RESPONSE = ClassName.get(HttpResponse.class);

  public JavaGenerator() {
  }

  public static Function<HttpRequest, HttpResponse> generateClass(String className, String functionBody) throws Exception {

    MethodSpec onRequest = MethodSpec.methodBuilder("onRequest")
      .addModifiers(PUBLIC, FINAL)
      .addParameter(HTTP_REQUEST, "request")
      .addCode(CodeBlock.builder().add(functionBody).build())
      .returns(HTTP_RESPONSE)
      .build();

    TypeSpec nsfsGeneratedClass = TypeSpec.classBuilder(className)
      .addModifiers(PUBLIC, FINAL)
      .addMethod(onRequest)
      .superclass(JavaBaseClass.class)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjeffrose.nsfs", nsfsGeneratedClass)
      .addStaticImport(io.netty.handler.codec.http.HttpHeaderNames.class, "*")
      .addStaticImport(java.util.BitSet.class, "*") // HACK!!
      .build();

    String source = javaFile.toString();
    // HACK!! {{{
    String imports = new StringBuilder()
      .append("import io.netty.handler.codec.http.*;")
      .append("\n")
      .append("import io.netty.buffer.*;")
      .append("\n")
      .toString();

    source = source.replace("import static java.util.BitSet.*;", imports);
    // HACK! }}}
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

}
