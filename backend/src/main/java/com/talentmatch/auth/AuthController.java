package com.talentmatch.auth;

import com.talentmatch.auth.dto.AuthResponse;
import com.talentmatch.auth.dto.LoginRequest;
import com.talentmatch.auth.dto.RegisterCompanyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Créer une entreprise et son administrateur")
    @PostMapping("/register-company")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        return authService.registerCompany(request);
    }

    @Operation(summary = "Se connecter et obtenir un jeton JWT")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
