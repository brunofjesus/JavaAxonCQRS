package pt.brunojesus.store.orderservice.command.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.brunojesus.store.orderservice.command.CreateOrderCommand;
import pt.brunojesus.store.orderservice.core.model.OrderStatus;
import pt.brunojesus.store.orderservice.core.model.OrderSummary;
import pt.brunojesus.store.orderservice.query.FindOrderQuery;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @Autowired
    public OrderCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public ResponseEntity<OrderSummary> createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {

        final String orderId = UUID.randomUUID().toString();

        final CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .productId(createOrderRestModel.getProductId())
                .addressId(createOrderRestModel.getAddressId())
                .quantity(createOrderRestModel.getQuantity())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .orderStatus(OrderStatus.CREATED)
                .build();

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = queryGateway.subscriptionQuery(
                new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class),
                ResponseTypes.instanceOf(OrderSummary.class)
        );

        try {
            commandGateway.sendAndWait(createOrderCommand);

            OrderSummary resultBody = queryResult.updates().blockFirst();
            return ResponseEntity.ok(resultBody);
        } finally {
            queryResult.close();
        }
    }
}
