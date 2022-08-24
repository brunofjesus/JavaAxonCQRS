package pt.brunojesus.store.orderservice.core.event;

import lombok.Value;
import pt.brunojesus.store.orderservice.core.model.OrderStatus;

@Value
public class OrderApprovedEvent {
    String orderId;
    OrderStatus orderStatus = OrderStatus.APPROVED;
}
