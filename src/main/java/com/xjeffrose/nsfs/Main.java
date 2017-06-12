package com.xjeffrose.nsfs;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    Server server = new Server();

    new Thread(new Server());

  }

}
