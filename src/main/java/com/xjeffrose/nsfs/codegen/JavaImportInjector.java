package com.xjeffrose.nsfs.codegen;

import java.util.ArrayList;
import java.util.List;

public class JavaImportInjector implements Appendable {

  private static String START_OF_PACKAGE = "package ";
  private static String END_OF_PACKAGE = ";\n";

  private List<String> importStrings = new ArrayList<String>();
  private StringBuilder builder = new StringBuilder();
  private boolean injected = false;

  public void addImport(String importString) {
    importStrings.add("import " + importString + ";\n");
  }

  private void injectImports() {
    if (!injected) {
      int packageIdx = builder.indexOf(START_OF_PACKAGE);
      if (packageIdx != -1) {
        int semiIdx = builder.indexOf(END_OF_PACKAGE, packageIdx);
        if (semiIdx != -1) {
          int insertPoint = semiIdx + END_OF_PACKAGE.length();
          StringBuilder importsBuilder = new StringBuilder().append("\n");
          for (String importString : importStrings) {
            importsBuilder.append(importString);
          }
          builder.insert(insertPoint, importsBuilder.toString());
          injected = true;
        }
      }
    }
  }

  public Appendable append(char c) {
    builder.append(c);
    injectImports();
    return this;
  }

  public Appendable append(CharSequence csq) {
    builder.append(csq);
    injectImports();
    return this;
  }

  public Appendable append(CharSequence csq, int start, int end) {
    builder.append(csq, start, end);
    injectImports();
    return this;
  }

  public String toString() {
    return builder.toString();
  }

}
