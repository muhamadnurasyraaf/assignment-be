package com.asyraaf.assignment.Product;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.Company;
import com.asyraaf.assignment.Company.CompanyService;
import com.asyraaf.assignment.Product.dto.CreateRequest;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CompanyService companyService;
    private final ProductRepository productRepository;
    private final UserService userService;

    private String generateSku(UUID companyId) {
        String companyName = companyService.getByIdForUpdate(companyId).getName();
        Long productCounts = productRepository.countByCompanyId(companyId);

        int length = companyName.length() > 2 ? 3 : companyName.length(); // just do this checking cuz to enforce
                                                                          // company name longer than 2 is not simpler
                                                                          // way

        return companyName.substring(0, length).toUpperCase() + productCounts;
    }

    @Transactional
    public Product createProduct(CreateRequest request, String email) {
        User user = userService.getByEmail(email);
        Company company = user.getCompany();

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user does not belong to a company");
        }

        String sku = generateSku(company.getId());

        Product product = Product.builder()
                .name(request.getName())
                .sku(sku)
                .company(company)
                .createdBy(user)
                .build();

        productRepository.save(product);

        return product;
    }
}
