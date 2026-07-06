package com.asyraaf.assignment.Company;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

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

        Company company = Company.builder().name(request.getName()).owner(user).build();

        companyRepository.save(company);

        return company;
    }
}
