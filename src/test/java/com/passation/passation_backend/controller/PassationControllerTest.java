package com.passation.passation_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.passation.passation_backend.dto.CreatePassationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PassationControllerTest {

    @Autowired private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getAllPassations_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/passations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice.martin@example.com", roles = "EMPLOYE")
    void getAllPassations_returns200_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/passations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getPassationById_returns200_whenExists() throws Exception {
        mockMvc.perform(get("/api/passations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getPassationById_returns5xx_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/passations/9999"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void createPassation_returns201_withValidData() throws Exception {
        CreatePassationRequest request = new CreatePassationRequest();
        request.setEmployePartantId(1L);
        request.setRemplacantId(3L);
        request.setManagerId(2L);
        request.setDateDepart(LocalDate.now().plusDays(30));
        request.setNotes("Test passation");

        mockMvc.perform(post("/api/passations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void createPassation_returns5xx_whenUserNotFound() throws Exception {
        CreatePassationRequest request = new CreatePassationRequest();
        request.setEmployePartantId(9999L);
        request.setRemplacantId(3L);
        request.setManagerId(2L);
        request.setDateDepart(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/passations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void getDashboardStats_returns200() throws Exception {
        mockMvc.perform(get("/api/passations/stats/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPassations").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void changerStatut_returns200_withValidStatut() throws Exception {
        mockMvc.perform(put("/api/passations/1/statut")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statut\":\"TERMINEE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getRiskScore_returns200_withValidId() throws Exception {
        mockMvc.perform(get("/api/passations/1/risk-score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.niveau").isNotEmpty())
                .andExpect(jsonPath("$.score").isNumber());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void searchPassations_returns200_withNoFilters() throws Exception {
        mockMvc.perform(get("/api/passations/search"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void searchPassations_returns200_withStatutFilter() throws Exception {
        mockMvc.perform(get("/api/passations/search?statut=EN_COURS"))
                .andExpect(status().isOk());
    }
}
