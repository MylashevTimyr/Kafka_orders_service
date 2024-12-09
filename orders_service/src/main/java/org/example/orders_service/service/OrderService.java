package org.example.orders_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.orders_service.mapper.OrderMapper;
import org.example.orders_service.model.Order;
import org.example.orders_service.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;
    private static final String NEW_ORDERS_TOPIC = "new-orders";

    @Transactional
    public Order createOrder(Order order) {
        log.info("Creating new order: {}", order);

        order.setStatus(Order.OrderStatus.NEW);
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved in DB with ID: {}, Status: {}", savedOrder.getId(), savedOrder.getStatus());

        OrderDTO orderDTO = OrderMapper.toOrderDTO(savedOrder);
        try {
            kafkaTemplate.send(NEW_ORDERS_TOPIC, orderDTO).get();
            log.info("Order sent to Kafka topic '{}': {}", NEW_ORDERS_TOPIC, orderDTO);
        } catch (Exception e) {
            log.error("Failed to send order to Kafka: {}", orderDTO, e);
            throw new RuntimeException("Failed to send order to Kafka", e);
        }

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, Order.OrderStatus status) {
        log.info("Updating status for order with ID: {} to {}", orderId, status);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        log.info("Order with ID: {} updated. New Status: {}", updatedOrder.getId(), updatedOrder.getStatus());
        return updatedOrder;
    }

    public List<Order> getAllOrders() {
        log.info("Retrieving all orders from the database");
        List<Order> orders = orderRepository.findAll();
        log.info("Retrieved {} orders", orders.size());
        return orders;
    }
}
