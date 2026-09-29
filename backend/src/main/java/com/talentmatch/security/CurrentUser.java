package com.talentmatch.security;

import com.talentmatch.user.Role;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("Aucun utilisateur authentifié dans le contexte de sécurité");
        }
        return user;
    }

    public static UUID userId() {
        return get().userId();
    }

    public static UUID tenantId() {
        return get().tenantId();
    }

    public static Role role() {
        return get().role();
    }
}
