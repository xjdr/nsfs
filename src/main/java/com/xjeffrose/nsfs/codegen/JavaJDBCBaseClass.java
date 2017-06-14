package com.xjeffrose.nsfs.codegen;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JavaJDBCBaseClass extends JavaBaseClass {

  public abstract Connection getConnection() throws SQLException, ClassNotFoundException;

}
