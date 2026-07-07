package com.asyraaf.assignment.Product;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asyraaf.assignment.Product.dto.CreateRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ApiResponse<Product> createProduct(@Valid @RequestBody CreateRequest request,
            @AuthenticationPrincipal String email) {

        Product product = productService.createProduct(request, email);

        return ApiResponse.success(product, "successfully created product");
    }
}
