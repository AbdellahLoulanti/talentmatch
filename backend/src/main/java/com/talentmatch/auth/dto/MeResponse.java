package com.talentmatch.auth.dto;

import com.talentmatch.tenant.Tenant;
import com.talentmatch.user.Role;
import com.talentmatch.user.User;
import java.util.UUID;

public record MeResponse(UUID userId, String email, Role role, TenantInfo tenant) {

    public record TenantInfo(UUID id, String name, String slug) {
    }

    public static MeResponse from(User user) {
        Tenant tenant = user.getTenant();
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                new TenantInfo(tenant.getId(), tenant.getName(), tenant.getSlug()));
    }
}
