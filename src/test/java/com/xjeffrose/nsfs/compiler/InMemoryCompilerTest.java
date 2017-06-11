package com.xjeffrose.nsfs.compiler;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.junit.Assert.*;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.lang.reflect.Method;
import org.junit.Test;

public class InMemoryCompilerTest {

  @Test
  public void compileBasicString() throws Exception {
    StringBuffer sourceCode = new StringBuffer();

    sourceCode.append("package com.xjdr.nsfs;\n");
    sourceCode.append("public class HelloClass {\n");
    sourceCode.append("   public String hello() { return \"hello\"; }");
    sourceCode.append("}");

    Class<?> helloClass = InMemoryCompiler.compile("com.xjdr.nsfs.HelloClass", sourceCode.toString());
    assertNotNull(helloClass);
    assertEquals(1, helloClass.getDeclaredMethods().length);

    Object o = helloClass.newInstance();
    Method m = helloClass.getDeclaredMethod("hello");

    assertEquals("hello", m.invoke(o));
  }

  @Test
  public void compileFromPoet() throws Exception {
    MethodSpec main = MethodSpec.methodBuilder("hello")
      .addModifiers(PUBLIC)
      .returns(String.class)
      .addStatement("return $S", "hello again")
      .build();

    TypeSpec helloPoet = TypeSpec.classBuilder("HelloClass")
      .addModifiers(PUBLIC, FINAL)
      .addMethod(main)
      .build();

    JavaFile javaFile = JavaFile.builder("com.xjdr.nsfs", helloPoet)
      .build();

    Class<?> helloClass = InMemoryCompiler.compile("com.xjdr.nsfs.HelloClass", javaFile.toString());
    assertNotNull(helloClass);
    assertEquals(1, helloClass.getDeclaredMethods().length);

    Object o = helloClass.newInstance();
    Method m = helloClass.getDeclaredMethod("hello");

    assertEquals("hello again", m.invoke(o));
  }
}
