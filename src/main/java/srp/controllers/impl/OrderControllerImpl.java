package srp.controllers.impl;
import srp.config.Paths;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import srp.controllers.OrderController;
import srp.models.Order;
import srp.repositories.OrderRepository;
import srp.utils.OrderCourierDispatcher;

public class OrderControllerImpl implements OrderController {
    private static final String ID = "id";

    private OrderRepository orderRepository;

    public OrderControllerImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void create(Context context) {
        Order order = context.bodyAsClass(Order.class);

        if (order.getId() != null) {
            throw new BadRequestResponse(String.format("Unable to create a new order with existing id: %s", order));
        }

        OrderCourierDispatcher orderCourierDispatcher = new OrderCourierDispatcher(order);
        String bestCourier = orderCourierDispatcher.getBestCourier();
        order.setCourier(bestCourier);

        orderRepository.create(order);
        context.status(HttpStatus.CREATED_201)
                .header(HttpHeader.LOCATION.name(), Paths.formatPostLocation(order.getId().toString()));

    }

    @Override
    public void find(Context context) {
        String id = context.pathParam(ID);
        Order order = orderRepository.find(id);

        if (order == null) {
            throw new NotFoundResponse(String.format("A order with id '%s' is not found", id));
        }

        context.json(order);

    }

    @Override
    public void findAll(Context context) {
        context.json(orderRepository.findAll());
    }
    
}