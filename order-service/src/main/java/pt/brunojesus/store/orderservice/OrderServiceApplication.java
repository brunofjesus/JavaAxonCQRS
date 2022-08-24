package pt.brunojesus.store.orderservice;

import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import pt.brunojesus.store.core.config.AxonConfig;

@SpringBootApplication
@Import({AxonConfig.class})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public DeadlineManager deadlineManager(Configuration configuration,
                                           SpringTransactionManager transactionManager) {
        return SimpleDeadlineManager.builder()
                .scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
                .transactionManager(transactionManager)
                .build();
    }
}
