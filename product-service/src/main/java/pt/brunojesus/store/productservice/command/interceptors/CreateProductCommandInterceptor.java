package pt.brunojesus.store.productservice.command.interceptors;

import org.apache.logging.log4j.util.Strings;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import pt.brunojesus.store.productservice.command.CreateProductCommand;
import pt.brunojesus.store.productservice.core.data.ProductLookupEntity;
import pt.brunojesus.store.productservice.core.data.ProductLookupRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger logger = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    @Autowired
    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {
        return (index, command) -> {

            logger.info("Intercepted command: " + command.getPayloadType());

            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                final CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(),
                        createProductCommand.getTitle()
                );

                if (productLookupEntity != null) {
                    throw new IllegalStateException(
                            String.format(
                                    "Product with productId %s or title %s already exists",
                                    createProductCommand.getProductId(),
                                    createProductCommand.getTitle()
                            )
                    );
                }

            }
            return command;
        };
    }
}
