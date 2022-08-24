package pt.brunojesus.store.orderservice.command;

import lombok.Builder;
import lombok.Data;
import pt.brunojesus.store.orderservice.core.model.OrderStatus;

@Data
@Builder
public class CreateOrderCommand {
    private final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;

}
