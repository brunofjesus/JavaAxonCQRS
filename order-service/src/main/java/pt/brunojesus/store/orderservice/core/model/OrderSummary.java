package pt.brunojesus.store.orderservice.core.model;

import lombok.Value;

@Value
public class OrderSummary {
    String orderId;
    OrderStatus orderStatus;
    String message;
}
