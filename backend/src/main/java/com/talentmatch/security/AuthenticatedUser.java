package com.talentmatch.security;

import com.talentmatch.user.Role;
import java.util.UUID;

public record AuthenticatedUser(UUID userId, UUID tenantId, Role role, String email) {
}
