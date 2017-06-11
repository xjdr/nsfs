package com.xjeffrose.nsfs.compiler;

import java.util.Arrays;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class InMemoryCompiler {
  static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

  public static Class<?> compile(String className, String sourceCodeInText) throws Exception {
    SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
    CompiledCode compiledCode = new CompiledCode(className);
    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
    DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());
    ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl);
    JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
    boolean result = task.call();
    return cl.loadClass(className);
  }

}
