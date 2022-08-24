package pt.brunojesus.store.paymentservice.query;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.core.event.PaymentProcessedEvent;
import pt.brunojesus.store.paymentservice.core.data.PaymentEntity;
import pt.brunojesus.store.paymentservice.core.data.PaymentRepository;

@Component
public class PaymentEventHandler {

    private final Logger logger = LoggerFactory.getLogger(PaymentEventHandler.class);
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentEventHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @EventHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent) {
        logger.info("PaymentProcessedEvent is called for order id: " + paymentProcessedEvent.getOrderId());
        final PaymentEntity payment = new PaymentEntity();
        payment.setOrderId(paymentProcessedEvent.getOrderId());
        payment.setPaymentId(paymentProcessedEvent.getPaymentId());

        paymentRepository.save(payment);
    }
}
