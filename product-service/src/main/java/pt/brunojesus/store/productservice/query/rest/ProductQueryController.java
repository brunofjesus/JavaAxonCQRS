package pt.brunojesus.store.productservice.query.rest;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.brunojesus.store.productservice.query.FindProductsQuery;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final QueryGateway queryGateway;

    @Autowired
    public ProductQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping
    public List<ProductRestModel> getProducts() {
        final FindProductsQuery findProductsQuery = new FindProductsQuery();

        return this.queryGateway.query(
                findProductsQuery,
                ResponseTypes.multipleInstancesOf(ProductRestModel.class)
        ).join();
    }
}
