package pt.brunojesus.store.paymentservice.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.ObjectUtils;
import pt.brunojesus.store.core.command.ProcessPaymentCommand;
import pt.brunojesus.store.core.event.PaymentProcessedEvent;

@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;


    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        if (ObjectUtils.isEmpty(processPaymentCommand.getPaymentDetails())) {
            throw new IllegalArgumentException("paymentDetails is required");
        }
        if (ObjectUtils.isEmpty(processPaymentCommand.getOrderId())) {
            throw new IllegalArgumentException("orderId is required");
        }
        if (ObjectUtils.isEmpty(processPaymentCommand.getPaymentId())) {
            throw new IllegalArgumentException("paymentId is required");
        }

        final PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(processPaymentCommand.getOrderId())
                .paymentId(processPaymentCommand.getPaymentId())
                .build();

        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        orderId = event.getOrderId();
        paymentId = event.getPaymentId();
    }
}
