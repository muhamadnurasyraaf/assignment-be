package com.asyraaf.assignment.Product.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateRequest {
    @NotBlank
    private String name;

    @NotNull
    private MultipartFile image;
}
