package com.asyraaf.assignment.Company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateRequest {
    @NotBlank
    private String name;
}
