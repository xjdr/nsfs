package com.xjeffrose.nsfs.http;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouteValidator {

  private RouteValidator() {}

  public static boolean isValid(String path) {
    try {
      URL u = new URL("http://localhost" + path);
      u.toURI();
      if (!u.getAuthority().equals("localhost")) {
        return false;
      }
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }

    if (path.contains("//")) {
      return false;
    }

    return true;
  }
}
