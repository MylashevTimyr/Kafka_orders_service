package org.example.shipping_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.shipping_service.mapper.OrderMapper;
import org.example.shipping_service.model.Order;
import org.example.shipping_service.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;
    private static final String SENT_ORDERS_TOPIC = "sent-orders";

    @KafkaListener(topics = "payed-service", groupId = "shipping-service")
    public void listenPayedOrders(OrderDTO orderDTO) {
        log.info("Received order for shipping from Kafka: {}", orderDTO.getId());

        try {
            processShipping(orderDTO);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to process shipping for order {}: {}", orderDTO.getId(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public void processShipping(OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (existingOrder.getStatus() == Order.OrderStatus.SHIPPED) {
            log.info("Order {} is already marked as SHIPPED.", orderDTO.getId());
            return;
        }

        log.info("Processing shipping for order {}", orderDTO.getId());
        existingOrder.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(existingOrder);

        kafkaTemplate.send(SENT_ORDERS_TOPIC, OrderMapper.toOrderDTO(existingOrder));
        log.info("Shipping processed for order {}", orderDTO.getId());
    }
}
