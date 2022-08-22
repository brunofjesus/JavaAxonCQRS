package pt.brunojesus.store.orderservice.core.event;

import lombok.Builder;
import lombok.Data;
import pt.brunojesus.store.orderservice.core.data.OrderStatus;

@Data
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
