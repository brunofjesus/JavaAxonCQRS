package pt.brunojesus.store.orderservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import pt.brunojesus.store.orderservice.core.data.OrderStatus;
import pt.brunojesus.store.orderservice.core.event.OrderApprovedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderCreatedEvent;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        final OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(createOrderCommand.getOrderId())
                .productId(createOrderCommand.getProductId())
                .userId(createOrderCommand.getUserId())
                .quantity(createOrderCommand.getQuantity())
                .addressId(createOrderCommand.getAddressId())
                .orderStatus(createOrderCommand.getOrderStatus())
                .build();

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());

        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    protected void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    protected void on(OrderApprovedEvent event) {
        this.orderStatus = event.getOrderStatus();
    }
}
