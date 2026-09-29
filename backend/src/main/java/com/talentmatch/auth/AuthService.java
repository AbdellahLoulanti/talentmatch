package com.talentmatch.auth;

import com.talentmatch.auth.dto.AuthResponse;
import com.talentmatch.auth.dto.LoginRequest;
import com.talentmatch.auth.dto.MeResponse;
import com.talentmatch.auth.dto.RegisterCompanyRequest;
import com.talentmatch.common.EmailAlreadyUsedException;
import com.talentmatch.common.InvalidCredentialsException;
import com.talentmatch.security.JwtService;
import com.talentmatch.tenant.Tenant;
import com.talentmatch.tenant.TenantRepository;
import com.talentmatch.user.Role;
import com.talentmatch.user.User;
import com.talentmatch.user.UserRepository;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final int SLUG_MAX_LENGTH = 90;

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        String email = normalizeEmail(request.adminEmail());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.companyName().trim());
        tenant.setSlug(uniqueSlug(request.companyName()));
        tenantRepository.save(tenant);

        User admin = new User();
        admin.setTenant(tenant);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRole(Role.TENANT_ADMIN);
        userRepository.save(admin);

        return toAuthResponse(admin);
    }

    /** Unknown e-mail, wrong password and inactive account all give the same error so accounts cannot be enumerated. */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
                .filter(User::isActive)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public MeResponse me(UUID userId) {
        return userRepository.findWithTenantById(userId)
                .filter(User::isActive)
                .map(MeResponse::from)
                .orElseThrow(InvalidCredentialsException::new);
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(jwtService.generate(user), TOKEN_TYPE, jwtService.expiresInSeconds());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String uniqueSlug(String companyName) {
        String base = slugify(companyName);
        String slug = base;
        for (int suffix = 2; tenantRepository.existsBySlug(slug); suffix++) {
            slug = base + "-" + suffix;
        }
        return slug;
    }

    private static String slugify(String value) {
        String slug = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (slug.length() > SLUG_MAX_LENGTH) {
            slug = slug.substring(0, SLUG_MAX_LENGTH).replaceAll("-+$", "");
        }
        return slug.isEmpty() ? "entreprise" : slug;
    }
}
