package com.asyraaf.assignment.User;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.asyraaf.assignment.Company.Company;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
