package com.talentmatch.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "admin@acme.ma") @NotBlank @Email String email,
        @Schema(example = "Password123!") @NotBlank String password) {
}
