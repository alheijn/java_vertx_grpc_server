package com.alheijn.java_vertx_grpc_server;

import com.ecommerce.ordermanagement.OrderManagement;
import com.ecommerce.ordermanagement.OrderManagementGrpc;
import com.ecommerce.ordermanagement.OrderManagementGrpcService;
import com.ecommerce.ordermanagement.OrderManagementService;
import io.vertx.core.AbstractVerticle;
import io.vertx.grpc.server.GrpcServer;
import java.util.logging.Logger;

public class VertxGrpcServer extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger(VertxGrpcServer.class.getName());

    OrderManagementServiceImpl service;

    private final int port = 9090;

    @Override
    public void start() throws Exception {
        logger.info("Creating grpc server");

        // create the server
        GrpcServer rpcServer = GrpcServer.server(vertx);
        rpcServer.callHandler(OrderManagementGrpcService.CreateOrder, request -> {
            request
                .last()
                .onSuccess(order -> {
                    service.createOrder(order);
                });
        });

        // start the server
        vertx.createHttpServer().requestHandler(rpcServer).listen(port)
            .onFailure(Throwable::printStackTrace);
        logger.info("Started grpc server on port " + port);
    }





}
