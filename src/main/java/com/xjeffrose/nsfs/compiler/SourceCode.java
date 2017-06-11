package com.xjeffrose.nsfs.compiler;

import java.io.IOException;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public class SourceCode extends SimpleJavaFileObject {
  private String contents = null;

  public SourceCode(String className, String contents) throws Exception {
    super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.contents = contents;
  }

  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
    return contents;
  }
}
