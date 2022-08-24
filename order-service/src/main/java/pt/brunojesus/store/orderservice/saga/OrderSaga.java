package pt.brunojesus.store.orderservice.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.brunojesus.store.core.command.ProcessPaymentCommand;
import pt.brunojesus.store.core.command.ReserveProductCommand;
import pt.brunojesus.store.core.event.PaymentProcessedEvent;
import pt.brunojesus.store.core.event.ProductReservedEvent;
import pt.brunojesus.store.core.model.User;
import pt.brunojesus.store.core.query.FetchUserPaymentDetailsQuery;
import pt.brunojesus.store.orderservice.command.ApproveOrderCommand;
import pt.brunojesus.store.orderservice.core.event.OrderApprovedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderCreatedEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {

    private static final Logger logger = LoggerFactory.getLogger(OrderSaga.class);

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {

        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        logger.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() +
                " and productId: " + reserveProductCommand.getProductId());

        commandGateway.send(reserveProductCommand, (commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                // Start a compensating transaction
                logger.error("ERROR: " + commandResultMessage.exceptionResult().getMessage(), commandResultMessage.exceptionResult());
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // Process user payment
        logger.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());

        final FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery(
                productReservedEvent.getUserId()
        );

        User userPaymentDetails = null;

        try {
            userPaymentDetails = queryGateway.query(
                    fetchUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)
            ).join();
        } catch (Exception ex) {
            logger.error(ex.getMessage());

            // Start compensating transaction
            return;
        }

        if (userPaymentDetails == null) {
            // Start compensating transaction
            return;
        }

        logger.info("Successfully fetched user payment details for  " + userPaymentDetails.getFirstName());

        final ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception ex) {
            logger.error(ex.getMessage());

            // Start compensating transaction
        }

        if (result == null) {
            logger.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");

            // Start compensating transaction
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        // Send an ApproveOrderCommand
        final ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        logger.info("Order is approved. OrderSaga completed for orderId: " + orderApprovedEvent.getOrderId());

        //SagaLifecycle.end();
    }
}
