package com.talentmatch.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    private static final String PASSWORD = "Password123!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerThenLoginThenMe() throws Exception {
        registerCompany("Acme SARL", "Admin@Acme.ma")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        String token = loginAndGetToken("admin@acme.ma", PASSWORD);

        mockMvc.perform(get("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@acme.ma"))
                .andExpect(jsonPath("$.role").value("TENANT_ADMIN"))
                .andExpect(jsonPath("$.tenant.name").value("Acme SARL"))
                .andExpect(jsonPath("$.tenant.slug").value("acme-sarl"));
    }

    @Test
    void registerWithAlreadyUsedEmailReturns409() throws Exception {
        registerCompany("Beta", "admin@beta.ma").andExpect(status().isCreated());

        registerCompany("Beta Bis", "admin@beta.ma")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void companiesWithSameNameGetDistinctSlugs() throws Exception {
        registerCompany("Société Générale Élite", "admin1@sge.ma").andExpect(status().isCreated());
        registerCompany("Société Générale Élite", "admin2@sge.ma").andExpect(status().isCreated());

        mockMvc.perform(get("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + loginAndGetToken("admin1@sge.ma", PASSWORD)))
                .andExpect(jsonPath("$.tenant.slug").value("societe-generale-elite"));
        mockMvc.perform(get("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + loginAndGetToken("admin2@sge.ma", PASSWORD)))
                .andExpect(jsonPath("$.tenant.slug").value("societe-generale-elite-2"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        registerCompany("Gamma", "admin@gamma.ma").andExpect(status().isCreated());

        login("admin@gamma.ma", "wrong-password").andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithUnknownEmailReturns401() throws Exception {
        login("nobody@nowhere.ma", PASSWORD).andExpect(status().isUnauthorized());
    }

    @Test
    void registerWithInvalidPayloadReturns400() throws Exception {
        registerCompany("", "not-an-email", "short")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.companyName").exists())
                .andExpect(jsonPath("$.errors.adminEmail").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void meWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void meWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/me").header(HttpHeaders.AUTHORIZATION, "Bearer not.a.valid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swaggerDocsArePublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"));
    }

    private ResultActions registerCompany(String companyName, String adminEmail) throws Exception {
        return registerCompany(companyName, adminEmail, PASSWORD);
    }

    private ResultActions registerCompany(String companyName, String adminEmail, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new RegisterCompanyPayload(companyName, adminEmail, password));
        return mockMvc.perform(post("/api/auth/register-company").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private ResultActions login(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new LoginPayload(email, password));
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String response = login(email, password)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private record RegisterCompanyPayload(String companyName, String adminEmail, String password) {
    }

    private record LoginPayload(String email, String password) {
    }
}
