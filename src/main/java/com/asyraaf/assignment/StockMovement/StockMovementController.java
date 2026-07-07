package com.asyraaf.assignment.StockMovement;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asyraaf.assignment.Company.dto.RecentTransactionsDto;
import com.asyraaf.assignment.StockMovement.dto.CreateRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stock-movement")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @PostMapping("/create")
    public ApiResponse<StockMovement> createMovement(@Valid @RequestBody CreateRequest request,
            @AuthenticationPrincipal String email) {

        StockMovement movement = stockMovementService.createMovement(request, email);

        return ApiResponse.success(movement, "successfully recorded stock movement");
    }

    @GetMapping
    public ApiResponse<List<RecentTransactionsDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal String email) {

        List<RecentTransactionsDto> movements = stockMovementService.getByCompany(email, page, size);

        return ApiResponse.success(movements, "fetched stock movements");
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<StockMovement>> getByProduct(@PathVariable UUID productId,
            @AuthenticationPrincipal String email) {

        List<StockMovement> movements = stockMovementService.getByProduct(productId, email);

        return ApiResponse.success(movements, "fetched stock movements");
    }
}
