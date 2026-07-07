package com.asyraaf.assignment.Company;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asyraaf.assignment.Company.dto.CreateRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    public ApiResponse<Company> createCompany(@Valid @RequestBody CreateRequest request,
            @AuthenticationPrincipal String email) {

        Company company = companyService.create(request, email);

        return ApiResponse.success(company, "succeed created company");

    }

    // @GetMapping("/dashboard")
    // public ApiResponse dashboardData(){

    // }

    @GetMapping("/me")
    public ApiResponse<Company> getMyCompany(@AuthenticationPrincipal String email) {
        Company company = companyService.getMine(email);

        if (company == null) {
            return ApiResponse.success(null, "user has no company yet");
        }

        return ApiResponse.success(company, "fetched company");
    }
}
