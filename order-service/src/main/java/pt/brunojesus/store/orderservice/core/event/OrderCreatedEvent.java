package pt.brunojesus.store.orderservice.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.brunojesus.store.orderservice.core.data.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
