package com.asyraaf.assignment.StockMovement.dto;

import java.util.UUID;

import com.asyraaf.assignment.StockMovement.StockMovementType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private StockMovementType type;

    @NotNull
    @Min(1)
    private Integer quantity;
}
