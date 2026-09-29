package com.talentmatch.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterCompanyRequest(
        @Schema(example = "Acme SARL") @NotBlank @Size(max = 150) String companyName,
        @Schema(example = "admin@acme.ma") @NotBlank @Email @Size(max = 255) String adminEmail,
        @Schema(example = "Password123!") @NotBlank @Size(min = 8, max = 72) String password) {
}
