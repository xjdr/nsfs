package com.xjeffrose.nsfs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppConfig {
  private final Config config = ConfigFactory.load();

  public AppConfig() {

  }

  public int readerIdleTimeout() {
    return config.getInt("reader_idle_timeout");
  }

  public int writerIdleTimeout() {
    return config.getInt("writer_idle_timeout");
  }

  public int requestIdleTimeout() {
    return config.getInt("request_idle_timeout");
  }

  public String workerNameFormat() {
    return config.getString("worker_name_format");
  }

  public int bossThreads() {
    return config.getInt("boss_threads");
  }

  public int workerThreads() {
    return config.getInt("worker_threads");
  }


  public int maxConnections() {
    return config.getInt("max_connections");
  }

  public float rateLimit() {
    return (float) config.getInt("req_per_sec");
  }


  public String cert() {
    return config.getString("cert");
  }

  public String key() {
    return config.getString("key");
  }

  public int port() {
    return config.getInt("server.port");
  }

}
