package com.xjeffrose.nsfs.codegen;

import static org.junit.Assert.*;

import com.squareup.javapoet.JavaFile;
import java.util.Arrays;
import java.util.Map;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.junit.Test;

public class JavaGeneratorTest {

  @Test
  public void generateClass() throws Exception {
    Map<String, JavaFile> classMap = JavaGenerator.generateClass("xx");
    String className = classMap.keySet().iterator().next();
    Iterable<JavaFileObject> compilationUnits = Arrays.asList(classMap.get(className).toJavaFileObject());

    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    JavaCompiler.CompilationTask task = javac.getTask(null, null, null, null, null, compilationUnits);
    boolean result = task.call();
    assertTrue(result);
    //ClassLoader cl = ClassLoader.getSystemClassLoader();
    //Class<?> helloClass = cl.loadClass("com.xjdr.nsfs.Hello");

    //assertNotNull(helloClass);

    //return ;
  }
}
