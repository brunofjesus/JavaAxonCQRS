package pt.brunojesus.store.core.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
@AllArgsConstructor
public class ReserveProductCommand {

    @TargetAggregateIdentifier
    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String userId;

}
