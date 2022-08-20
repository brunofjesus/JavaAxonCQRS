package pt.brunojesus.store.productservice.query;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.productservice.core.data.ProductEntity;
import pt.brunojesus.store.productservice.core.data.ProductRepository;
import pt.brunojesus.store.productservice.core.event.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {
    //This could also have been called ProductProjection

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
        ProductEntity productEntity = new ProductEntity();
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
}
