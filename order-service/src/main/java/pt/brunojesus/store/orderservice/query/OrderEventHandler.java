package pt.brunojesus.store.orderservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.orderservice.core.data.OrderEntity;
import pt.brunojesus.store.orderservice.core.data.OrderRepository;
import pt.brunojesus.store.orderservice.core.event.OrderCreatedEvent;

@Component
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        final OrderEntity orderEntity = OrderEntity.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .addressId(orderCreatedEvent.getAddressId())
                .orderStatus(orderCreatedEvent.getOrderStatus())
                .userId(orderCreatedEvent.getUserId())
                .build();

        orderRepository.save(orderEntity);
    }
}
