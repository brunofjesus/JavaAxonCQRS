package pt.brunojesus.store.productservice.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.core.event.ProductReservationCancelledEvent;
import pt.brunojesus.store.core.event.ProductReservedEvent;
import pt.brunojesus.store.productservice.core.data.ProductEntity;
import pt.brunojesus.store.productservice.core.data.ProductRepository;
import pt.brunojesus.store.productservice.core.event.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {
    //This could also have been called ProductProjection

    private static final Logger logger = LoggerFactory.getLogger(ProductEventHandler.class);

    private final ProductRepository productRepository;

    @Autowired
    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Handles IllegalArgumentExceptions in this class
     * @param exception
     */
    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {
        // Log error messages
    }

    /**
     * Handles Exception in this class
     * @param exception
     */
    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        final ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productRepository.save(productEntity);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

//        if (true) {
//            throw new Exception("Forcing exception in the Event Handler class");
//        }
    }

    @EventHandler
    public void on(ProductReservedEvent event) {
        final ProductEntity productEntity = productRepository.findByProductId(event.getProductId());

        logger.debug("ProductReservedEvent: Current product quantity " + productEntity.getQuantity());

        productEntity.setQuantity(productEntity.getQuantity() - event.getQuantity());

        productRepository.save(productEntity);

        logger.debug("ProductReservedEvent: New product quantity " + productEntity.getQuantity());

        logger.info("ProductReservedEvent is called for productId: " + event.getProductId() +
                " and orderId: " + event.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent event) {
        final ProductEntity productEntity = productRepository.findByProductId(event.getProductId());

        logger.debug("ProductReservationCancelledEvent: Current product quantity " + productEntity.getQuantity());

        productEntity.setQuantity(productEntity.getQuantity() + event.getQuantity());

        productRepository.save(productEntity);

        logger.debug("ProductReservationCancelledEvent: New product quantity " + productEntity.getQuantity());

        logger.info("ProductReservationCancelledEvent is called for productId: " + event.getProductId() +
                " and orderId: " + event.getOrderId());
    }

    @ResetHandler
    public void reset() {
        productRepository.deleteAll();
    }
}
