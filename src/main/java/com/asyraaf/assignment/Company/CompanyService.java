package com.asyraaf.assignment.Company;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.dto.CreateRequest;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    public Company create(CreateRequest request, String email) {
        User user = userService.getByEmail(email);

        if (user.getCompany() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user already belongs to a company");
        }

        Company company = Company.builder().name(request.getName()).owner(user).build();

        companyRepository.save(company);

        userService.assignCompany(user, company);

        return company;
    }

    public Company getById(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "company not found"));
    }

    public Company getByIdForUpdate(UUID id) {
        return companyRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "company not found"));
    }

    public Company getMine(String email) {
        User user = userService.getByEmail(email);
        return user.getCompany();
    }
}
