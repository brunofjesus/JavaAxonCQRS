package pt.brunojesus.store.orderservice.command.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.brunojesus.store.orderservice.command.CreateOrderCommand;
import pt.brunojesus.store.orderservice.core.data.OrderStatus;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final CommandGateway commandGateway;

    @Autowired
    public OrderCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {

        final CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(UUID.randomUUID().toString())
                .productId(createOrderRestModel.getProductId())
                .addressId(createOrderRestModel.getAddressId())
                .quantity(createOrderRestModel.getQuantity())
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .orderStatus(OrderStatus.CREATED)
                .build();

        return commandGateway.sendAndWait(createOrderCommand);
    }
}
