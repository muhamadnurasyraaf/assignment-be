package com.asyraaf.assignment.Company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.asyraaf.assignment.StockMovement.StockMovement;
import com.asyraaf.assignment.StockMovement.StockMovementType;

public record RecentTransactionsDto(
        UUID id,
        String productName,
        StockMovementType type,
        Integer quantity,
        String performedBy,
        LocalDateTime createdAt) {

    public static RecentTransactionsDto from(StockMovement movement) {
        return new RecentTransactionsDto(
                movement.getId(),
                movement.getProduct().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getUser().getUsername(),
                movement.getCreatedAt());
    }
}
