package com.alheijn.java_vertx_grpc_server;

import com.ecommerce.ordermanagement.CreateOrderRequest;
import com.ecommerce.ordermanagement.Order;
import com.ecommerce.ordermanagement.OrderManagementGrpcService;
import com.ecommerce.ordermanagement.ShipmentEvent;
import com.ecommerce.ordermanagement.Status;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.grpc.server.GrpcServer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerVerticle extends VerticleBase {

    @Override
    public Future<?> start() {
        OrderManagementGrpcService service  = new OrderManagementGrpcService() {
            @Override
            public Future<Order> createOrder(CreateOrderRequest request) {
                String orderId = UUID.randomUUID().toString();
                Instant currentTime = Instant.now();
                List<ShipmentEvent> shipmentHistory = new ArrayList<>();

                ShipmentEvent event1 = ShipmentEvent.newBuilder()
                    .setDescription("Order created.")
                    .setLocation("Warehouse A")
                    .setEventTimestamp(currentTime.getEpochSecond())
                    .build();

                ShipmentEvent event2 = ShipmentEvent.newBuilder()
                    .setDescription("Order is being processed.")
                    .setLocation("Warehouse A")
                    .setEventTimestamp(currentTime.plusSeconds(60).getEpochSecond())
                    .build();

                shipmentHistory.add(event1);
                shipmentHistory.add(event2);

                // 2. Calculate total price
                double totalPrice = request.getItemsList().stream()
                    .mapToDouble(item -> item.getPricePerUnit() * item.getQuantity())
                    .sum();

                // 3. Create Order object to be returned
                Order order = Order.newBuilder()
                    .setOrderId(orderId)
                    .setCustomerId(request.getCustomerId())
                    .addAllItems(request.getItemsList())
                    .setShippingAddress(request.getShippingAddress())
                    .setTotalPrice(totalPrice)
                    .setStatus(Status.PENDING)
                    .setCreatedAtTimestamp(Instant.now().getEpochSecond())
                    .addAllShipmentHistory(shipmentHistory) // Attach the new field
                    .build();

                return Future.succeededFuture(
                    order
                );
            }
        };

        GrpcServer grpcServer = GrpcServer.server(vertx);
        grpcServer.addService(service);

        return vertx.createHttpServer()
            .requestHandler(grpcServer)
            .listen(50051);
    };

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new ServerVerticle()).await();
        System.out.println("Verticle deployed, server started");
    }

}
