package pt.brunojesus.store.orderservice.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pt.brunojesus.store.core.command.CancelProductReservationCommand;
import pt.brunojesus.store.core.command.ProcessPaymentCommand;
import pt.brunojesus.store.core.command.ReserveProductCommand;
import pt.brunojesus.store.core.event.PaymentProcessedEvent;
import pt.brunojesus.store.core.event.ProductReservationCancelledEvent;
import pt.brunojesus.store.core.event.ProductReservedEvent;
import pt.brunojesus.store.core.model.User;
import pt.brunojesus.store.core.query.FetchUserPaymentDetailsQuery;
import pt.brunojesus.store.orderservice.command.ApproveOrderCommand;
import pt.brunojesus.store.orderservice.command.RejectOrderCommand;
import pt.brunojesus.store.orderservice.core.event.OrderApprovedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderCreatedEvent;
import pt.brunojesus.store.orderservice.core.event.OrderRejectedEvent;

import java.time.Duration;
import java.util.UUID;

@Saga
public class OrderSaga {

    private static final Logger logger = LoggerFactory.getLogger(OrderSaga.class);

    private static final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private String scheduleId;

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
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            // Start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details");
            return;
        }

        logger.info("Successfully fetched user payment details for  " + userPaymentDetails.getFirstName());

        scheduleId = deadlineManager.schedule(
                Duration.ofSeconds(120),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE,
                productReservedEvent
        );

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
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (result == null) {
            logger.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");

            // Start compensating transaction
            cancelProductReservation(
                    productReservedEvent,
                    "Could not process user payment with provided payment details"
            );
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {
        cancelDeadline();

        final CancelProductReservationCommand command = CancelProductReservationCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .productId(productReservedEvent.getProductId())
                .quantity(productReservedEvent.getQuantity())
                .userId(productReservedEvent.getUserId())
                .reason(reason)
                .build();

        commandGateway.send(command);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        cancelDeadline();

        // Send an ApproveOrderCommand
        final ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
            //deadlineManager.cancelAll(PAYMENT_PROCESSING_TIMEOUT_DEADLINE);
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        logger.info("Order is approved. OrderSaga completed for orderId: " + orderApprovedEvent.getOrderId());

        //SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent event) {
        // Create and send a RejectOrderCommand
        final RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId(), event.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        logger.info("Successfully rejected order with id: " + orderRejectedEvent.getOrderId());
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        logger.info("Payment processing deadline took place, sending compensating command to cancel the reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }
}
