package org.example.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orders_service.dto.OrderDTO;
import org.example.payment_service.mapper.OrderMapper;
import org.example.payment_service.model.Order;
import org.example.payment_service.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;
    private static final String PAYED_ORDERS_TOPIC = "payed-service";

    @KafkaListener(topics = "new-orders", groupId = "payment-service")
    public void listenNewOrders(OrderDTO orderDTO) {
        log.info("Received order from Kafka: {}", orderDTO);

        try {
            processPayment(orderDTO);
        } catch (IllegalArgumentException ex) {
            log.error("Order processing failed: {}", ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public void processPayment(OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (existingOrder.getStatus() == Order.OrderStatus.PAYED) {
            log.info("Order {} is already marked as PAYED.", orderDTO.getId());
            return;
        }

        log.info("Processing payment for order {}", orderDTO.getId());
        existingOrder.setStatus(Order.OrderStatus.PAYED);
        orderRepository.save(existingOrder);

        kafkaTemplate.send(PAYED_ORDERS_TOPIC, OrderMapper.toOrderDTO(existingOrder));
        log.info("Payment processed for order {}", orderDTO.getId());
    }
}
