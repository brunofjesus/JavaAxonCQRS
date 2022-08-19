package pt.brunojesus.store.productservice.command.interceptors;

import org.apache.logging.log4j.util.Strings;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.productservice.command.CreateProductCommand;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This is redundant, and it's only here for demo purposes
 */
@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger logger = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {
        return (index, command) -> {

            logger.info("Intercepted command: " + command.getPayloadType());

            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                // This is a copy of the validation being done in the aggregate
                // it's purpose is to demonstrate that we can also do validations with an Interceptor
                final CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price cannot be 0 or less");
                }

                if (Strings.isBlank(createProductCommand.getTitle())) {
                    throw new IllegalArgumentException("Title cannot be empty");
                }
            }

            return command;
        };
    }
}
