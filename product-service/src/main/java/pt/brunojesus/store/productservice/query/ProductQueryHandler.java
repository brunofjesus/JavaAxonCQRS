package pt.brunojesus.store.productservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.productservice.core.data.ProductRepository;
import pt.brunojesus.store.productservice.query.rest.ProductRestModel;

import java.util.List;

@Component
public class ProductQueryHandler {

    private final ProductRepository productRepository;

    @Autowired
    public ProductQueryHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery findProductsQuery) {
        return productRepository.findAll().stream()
                .map(entity -> ProductRestModel.builder()
                        .productId(entity.getProductId())
                        .title(entity.getTitle())
                        .price(entity.getPrice())
                        .quantity(entity.getQuantity()).build()
                ).toList();
    }
}
