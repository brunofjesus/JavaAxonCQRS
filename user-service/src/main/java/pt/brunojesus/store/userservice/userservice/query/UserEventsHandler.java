package pt.brunojesus.store.userservice.userservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.core.model.PaymentDetails;
import pt.brunojesus.store.core.model.User;
import pt.brunojesus.store.core.query.FetchUserPaymentDetailsQuery;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User handle(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        final PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("BRUNO JESUS")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        return User.builder()
                .firstName("Bruno")
                .lastName("Jesus")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();
    }
}
