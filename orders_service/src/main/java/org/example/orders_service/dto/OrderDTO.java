package org.example.orders_service.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    private UUID id;
    private String itemId;
    private String userId;
    private BigDecimal amount;
    private String status;
}
