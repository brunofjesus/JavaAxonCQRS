package pt.brunojesus.store.orderservice.core.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.brunojesus.store.orderservice.core.model.OrderStatus;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    @Column(unique = true)
    private String orderId;

    private String productId;

    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

}
