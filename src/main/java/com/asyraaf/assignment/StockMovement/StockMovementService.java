package com.asyraaf.assignment.StockMovement;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.Company;
import com.asyraaf.assignment.Company.dto.RecentTransactionsDto;
import com.asyraaf.assignment.Product.Product;
import com.asyraaf.assignment.Product.ProductService;
import com.asyraaf.assignment.StockMovement.dto.CreateRequest;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final UserService userService;

    private Company requireCompany(User user) {
        Company company = user.getCompany();

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user does not belong to a company");
        }

        return company;
    }

    private void requireSameCompany(Product product, Company company) {
        if (!product.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "product does not belong to your company");
        }
    }

    @Transactional
    public StockMovement createMovement(CreateRequest request, String email) {
        User user = userService.getByEmail(email);
        Company company = requireCompany(user);

        Product product = productService.getById(request.getProductId());
        requireSameCompany(product, company);

        int delta = request.getType() == StockMovementType.OUT ? -request.getQuantity() : request.getQuantity();
        productService.adjustQuantity(product.getId(), delta);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(request.getType())
                .quantity(request.getQuantity())
                .user(user)
                .build();

        stockMovementRepository.save(movement);

        return movement;
    }

    public List<StockMovement> getByProduct(UUID productId, String email) {
        User user = userService.getByEmail(email);
        Company company = requireCompany(user);

        Product product = productService.getById(productId);
        requireSameCompany(product, company);

        return stockMovementRepository.findByProductId(productId);
    }

    public List<StockMovement> getLatestByCompany(UUID companyId) {
        return stockMovementRepository.findLatestByCompanyId(companyId, PageRequest.of(0, 5));
    }

    public List<RecentTransactionsDto> getByCompany(String email, int page, int size) {
        User user = userService.getByEmail(email);
        Company company = requireCompany(user);

        List<StockMovement> movements = stockMovementRepository.findLatestByCompanyId(company.getId(),
                PageRequest.of(page, size));

        return movements.stream().map(RecentTransactionsDto::from).toList();
    }
}
