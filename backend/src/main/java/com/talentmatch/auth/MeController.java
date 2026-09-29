package com.talentmatch.auth;

import com.talentmatch.auth.dto.MeResponse;
import com.talentmatch.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Tag(name = "Utilisateur connecté")
public class MeController {

    private final AuthService authService;

    @Operation(summary = "Informations de l'utilisateur connecté et de son entreprise")
    @GetMapping
    public MeResponse me() {
        return authService.me(CurrentUser.userId());
    }
}
