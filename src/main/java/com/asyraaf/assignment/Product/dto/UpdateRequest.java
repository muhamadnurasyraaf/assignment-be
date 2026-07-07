package com.asyraaf.assignment.Product.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UpdateRequest {
    private String name;

    private MultipartFile image;

    @Min(0)
    private Integer quantity;
}
