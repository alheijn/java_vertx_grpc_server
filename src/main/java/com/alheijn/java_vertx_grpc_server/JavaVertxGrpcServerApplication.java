package com.alheijn.java_vertx_grpc_server;

import io.vertx.core.Vertx;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaVertxGrpcServerApplication {

    @Autowired
    private VertxGrpcServer vertxGrpcServer;

    public static void main(String[] args) {
        SpringApplication.run(JavaVertxGrpcServerApplication.class, args);
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(vertxGrpcServer);
    }

}
