package pt.brunojesus.store.orderservice.core.event;

import lombok.Value;
import pt.brunojesus.store.orderservice.core.data.OrderStatus;

@Value
public class OrderApprovedEvent {
    String orderId;
    OrderStatus orderStatus = OrderStatus.APPROVED;
}
