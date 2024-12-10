package org.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class OrderDTO implements Serializable {
    private UUID id;
    private String itemId;
    private String userId;
    private BigDecimal amount;
    private String status;

    public OrderDTO() {}

    public OrderDTO(UUID id, String itemId, String userId, BigDecimal amount, String status) {
        this.id = id;
        this.itemId = itemId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", itemId='" + itemId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }
}

