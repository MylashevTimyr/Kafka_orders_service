package org.example.notification_service.mapper;

import lombok.experimental.UtilityClass;
import org.example.notification_service.model.Order;
import org.example.dto.OrderDTO;

@UtilityClass
public class OrderMapper {

    public static OrderDTO toOrderDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getItemId(),
                order.getAmount(),
                order.getStatus().name()
        );
    }

    public static Order toOrderEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setUserId(orderDTO.getUserId());
        order.setItemId(orderDTO.getItemId());
        order.setAmount(orderDTO.getAmount());
        order.setStatus(Order.OrderStatus.valueOf(orderDTO.getStatus()));
        return order;
    }
}
