package pt.brunojesus.store.orderservice.query;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.brunojesus.store.orderservice.core.data.OrderEntity;
import pt.brunojesus.store.orderservice.core.data.OrderRepository;
import pt.brunojesus.store.orderservice.core.model.OrderSummary;

/**
 * Looks like this isn't needed
 */
@Component
public class OrderQueriesHandler {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderQueriesHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
        final OrderEntity entity = this.orderRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(entity.getOrderId(), entity.getOrderStatus(), "");
    }
}
