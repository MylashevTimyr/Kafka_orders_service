package org.example.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.mapper.OrderMapper;
import org.example.notification_service.model.Order;
import org.example.notification_service.repository.OrderRepository;
import org.example.orders_service.dto.OrderDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "sent-orders", groupId = "notification-service")
    public void listenSentOrders(OrderDTO orderDTO) {
        log.info("Received order for notification: {}", orderDTO.getId());

        try {
            processNotification(orderDTO);
        } catch (IllegalArgumentException ex) {
            log.error("Failed to process notification for order {}: {}", orderDTO.getId(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public void processNotification(OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(orderDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (existingOrder.getStatus() == Order.OrderStatus.COMPLETED) {
            log.info("Order {} is already marked as COMPLETED.", orderDTO.getId());
            return;
        }

        log.info("Processing notification for order {}", orderDTO.getId());
        existingOrder.setStatus(Order.OrderStatus.COMPLETED);
        orderRepository.save(existingOrder);

        sendNotification(OrderMapper.toOrderDTO(existingOrder));
        log.info("Notification sent for order {}", orderDTO.getId());
    }

    private void sendNotification(OrderDTO orderDTO) {
        log.info("Sending notification to user {} about order: {}", orderDTO.getUserId(), orderDTO.getId());
    }
}
