package pt.brunojesus.store.orderservice.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class RejectOrderCommand {

    @TargetAggregateIdentifier
    String orderId;
    String reason;
}
