package com.asyraaf.assignment.Company;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.dto.ActiveUsersDto;
import com.asyraaf.assignment.Company.dto.DashboardResponse;
import com.asyraaf.assignment.Company.dto.LatestProductsDto;
import com.asyraaf.assignment.Company.dto.RecentTransactionsDto;
import com.asyraaf.assignment.Product.Product;
import com.asyraaf.assignment.Product.ProductService;
import com.asyraaf.assignment.StockMovement.StockMovement;
import com.asyraaf.assignment.StockMovement.StockMovementService;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final CompanyService companyService;
    private final ProductService productService;
    private final UserService userService;
    private final StockMovementService stockMovementService;

    @Qualifier("dashboardExecutor")
    private final Executor dashboardExecutor;

    public DashboardResponse getDashboard(String email) {
        Company company = companyService.getMine(email);

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user does not belong to a company");
        }

        UUID companyId = company.getId();

        CompletableFuture<List<RecentTransactionsDto>> transactionsFuture = CompletableFuture
                .supplyAsync(() -> stockMovementService.getLatestByCompany(companyId), dashboardExecutor)
                .thenApply(DashboardService::toRecentTransactionsDto);

        CompletableFuture<List<LatestProductsDto>> productsFuture = CompletableFuture
                .supplyAsync(() -> productService.getLatestByCompany(companyId), dashboardExecutor)
                .thenApply(DashboardService::toLatestProductsDto);

        CompletableFuture<List<ActiveUsersDto>> usersFuture = CompletableFuture
                .supplyAsync(() -> userService.getLatestByCompany(companyId), dashboardExecutor)
                .thenApply(DashboardService::toActiveUsersDto);

        CompletableFuture.allOf(transactionsFuture, productsFuture, usersFuture).join();

        return new DashboardResponse(
                company.getName(),
                transactionsFuture.join(),
                productsFuture.join(),
                usersFuture.join());
    }

    private static List<RecentTransactionsDto> toRecentTransactionsDto(List<StockMovement> movements) {
        return movements.stream()
                .map(movement -> new RecentTransactionsDto(
                        movement.getId(),
                        movement.getProduct().getName(),
                        movement.getType(),
                        movement.getQuantity(),
                        movement.getUser().getUsername(),
                        movement.getCreatedAt()))
                .toList();
    }

    private static List<LatestProductsDto> toLatestProductsDto(List<Product> products) {
        return products.stream()
                .map(product -> new LatestProductsDto(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getImageUrl(),
                        product.getQuantity(),
                        product.getCreatedAt()))
                .toList();
    }

    private static List<ActiveUsersDto> toActiveUsersDto(List<User> users) {
        return users.stream()
                .map(user -> new ActiveUsersDto(user.getUsername(), user.getEmail(), user.getRole()))
                .toList();
    }
}
