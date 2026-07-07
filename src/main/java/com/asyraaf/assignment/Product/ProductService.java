package com.asyraaf.assignment.Product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.Company;
import com.asyraaf.assignment.Company.CompanyService;
import com.asyraaf.assignment.Product.dto.CreateRequest;
import com.asyraaf.assignment.Product.dto.UpdateRequest;
import com.asyraaf.assignment.StockMovement.StockMovementRepository;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;
import com.asyraaf.assignment.common.service.CloudinaryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CompanyService companyService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final StockMovementRepository stockMovementRepository;

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
        Company company = requireCompany(user);

        String imageUrl = cloudinaryService.uploadImage(request.getImage());
        String sku = generateSku(company.getId());

        Product product = Product.builder()
                .name(request.getName())
                .imageUrl(imageUrl)
                .sku(sku)
                .quantity(request.getQuantity() != null ? request.getQuantity() : 0)
                .company(company)
                .createdBy(user)
                .build();

        productRepository.save(product);

        return product;
    }

    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }

    @Transactional
    public void adjustQuantity(UUID id, int delta) {
        int updated = productRepository.adjustQuantity(id, delta);

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "insufficient stock for this movement");
        }
    }

    public List<Product> getLatestByCompany(UUID companyId) {
        return productRepository.findTop5ByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public List<Product> getAllByCompany(String email) {
        User user = userService.getByEmail(email);
        Company company = requireCompany(user);

        return productRepository.findByCompanyId(company.getId());
    }

    public Product getByIdForCompany(UUID id, String email) {
        User user = userService.getByEmail(email);
        Company company = requireCompany(user);

        Product product = getById(id);
        requireSameCompany(product, company);

        return product;
    }

    @Transactional
    public Product updateProduct(UUID id, UpdateRequest request, String email) {
        Product product = getByIdForCompany(id, email);

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            product.setImageUrl(cloudinaryService.uploadImage(request.getImage()));
        }

        productRepository.save(product);

        return product;
    }

    @Transactional
    public void deleteProduct(UUID id, String email) {
        Product product = getByIdForCompany(id, email);

        if (stockMovementRepository.existsByProductId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "cannot delete a product with recorded stock movement history");
        }

        productRepository.delete(product);
    }
}
