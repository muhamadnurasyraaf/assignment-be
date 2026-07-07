package com.asyraaf.assignment.User;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.Company;
import com.asyraaf.assignment.Product.ProductRepository;
import com.asyraaf.assignment.StockMovement.StockMovementRepository;
import com.asyraaf.assignment.User.dto.UpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    private Company requireCompany(User user) {
        Company company = user.getCompany();

        if (company == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user does not belong to a company");
        }

        return company;
    }

    public User createUser(String username, String email, String password, Role role, Company company) {

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .company(company)
                .build();

        userRepository.save(user);

        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found with email: " + email));
    }

    public User assignCompany(User user, Company company) {
        user.setCompany(company);
        return userRepository.save(user);
    }

    public List<User> getLatestByCompany(UUID companyId) {
        return userRepository.findTop5ByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public List<User> getAllByCompany(String email) {
        User caller = getByEmail(email);
        Company company = requireCompany(caller);

        return userRepository.findByCompanyId(company.getId());
    }

    public User getByIdForCompany(UUID id, String email) {
        User caller = getByEmail(email);
        Company company = requireCompany(caller);

        return userRepository.findById(id)
                .filter(user -> user.getCompany() != null && user.getCompany().getId().equals(company.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

    @Transactional
    public User updateUser(UUID id, UpdateRequest request, String email) {
        User user = getByIdForCompany(id, email);

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id, String email) {
        User caller = getByEmail(email);
        User user = getByIdForCompany(id, email);

        if (user.getId().equals(caller.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "cannot delete your own account");
        }

        if (productRepository.existsByCreatedById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "cannot delete a user who has created products");
        }

        if (stockMovementRepository.existsByUserId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "cannot delete a user with recorded stock movement history");
        }

        userRepository.delete(user);
    }
}
