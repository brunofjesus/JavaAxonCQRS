package pt.brunojesus.store.orderservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.orderservice.core.data.OrderEntity;
import pt.brunojesus.store.orderservice.core.data.OrderRepository;
import pt.brunojesus.store.orderservice.core.event.OrderApprovedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderCreatedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderRejectedEvent;

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

    @EventHandler
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        final OrderEntity order = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());

        if (order == null) {
            // TODO: Do something about it
            return;
        }

        order.setOrderStatus(orderApprovedEvent.getOrderStatus());

        orderRepository.save(order);
    }

    @EventHandler
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        final OrderEntity order = orderRepository.findByOrderId(orderRejectedEvent.getOrderId());
        order.setOrderStatus(orderRejectedEvent.getOrderStatus());
        orderRepository.save(order);
    }
}
