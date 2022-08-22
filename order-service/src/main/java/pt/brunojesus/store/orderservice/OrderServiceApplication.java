package pt.brunojesus.store.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import pt.brunojesus.store.core.config.AxonConfig;

@SpringBootApplication
@Import({AxonConfig.class})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
