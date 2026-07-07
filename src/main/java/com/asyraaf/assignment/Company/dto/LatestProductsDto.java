package com.asyraaf.assignment.Company.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LatestProductsDto(
        UUID id,
        String name,
        String sku,
        String imageUrl,
        Integer quantity,
        LocalDateTime createdAt) {

}
