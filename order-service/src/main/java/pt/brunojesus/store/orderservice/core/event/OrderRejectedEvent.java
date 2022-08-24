package pt.brunojesus.store.orderservice.core.event;

import lombok.Value;
import pt.brunojesus.store.orderservice.core.model.OrderStatus;

@Value
public class OrderRejectedEvent {
    String orderId;
    String reason;
    OrderStatus orderStatus = OrderStatus.REJECTED;
}
