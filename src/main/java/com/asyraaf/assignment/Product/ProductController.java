package com.asyraaf.assignment.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asyraaf.assignment.Product.dto.CreateRequest;
import com.asyraaf.assignment.Product.dto.UpdateRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Product> createProduct(@Valid @ModelAttribute CreateRequest request,
            @AuthenticationPrincipal String email) {

        Product product = productService.createProduct(request, email);

        return ApiResponse.success(product, "successfully created product");
    }

    @GetMapping
    public ApiResponse<List<Product>> getAllProducts(@AuthenticationPrincipal String email) {
        List<Product> products = productService.getAllByCompany(email);

        return ApiResponse.success(products, "fetched products");
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable UUID id, @AuthenticationPrincipal String email) {
        Product product = productService.getByIdForCompany(id, email);

        return ApiResponse.success(product, "fetched product");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Product> updateProduct(@PathVariable UUID id, @Valid @ModelAttribute UpdateRequest request,
            @AuthenticationPrincipal String email) {

        Product product = productService.updateProduct(id, request, email);

        return ApiResponse.success(product, "successfully updated product");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable UUID id, @AuthenticationPrincipal String email) {
        productService.deleteProduct(id, email);

        return ApiResponse.success(null, "successfully deleted product");
    }
}
