package pt.brunojesus.store.productservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.productservice.core.data.ProductEntity;
import pt.brunojesus.store.productservice.core.data.ProductRepository;
import pt.brunojesus.store.productservice.core.event.ProductCreatedEvent;

@Component
public class ProductEventHandler {
    //This could also have been called ProductProjection

    private final ProductRepository productRepository;

    @Autowired
    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        productRepository.save(productEntity);
    }
}
