package pt.brunojesus.store.productservice.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private Environment env;

    @PostMapping
    public String createProduct() {
        return "HTTP POST handled";
    }

    @GetMapping
    public String getProduct() {
        return "HTTP GET handled " + env.getProperty("local.server.port");
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT handled";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE handled";
    }
}
