package com.passation.passation_backend.controller;

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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AlerteControllerTest {

    @Autowired private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getAlertes_returns401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/alertes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getAll_returns200_withList() throws Exception {
        mockMvc.perform(get("/api/alertes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getNonLues_returns200() throws Exception {
        mockMvc.perform(get("/api/alertes/non-lues"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void getByPassation_returns200_withKnownPassation() throws Exception {
        mockMvc.perform(get("/api/alertes/passation/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void marquerLue_returns200_whenAlerteExists() throws Exception {
        mockMvc.perform(put("/api/alertes/1/lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lu").value(true));
    }

    @Test
    @WithMockUser(roles = "EMPLOYE")
    void marquerLue_returns5xx_whenAlerteNotFound() throws Exception {
        mockMvc.perform(put("/api/alertes/9999/lu"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void generateAlertes_returns200_withKnownPassation() throws Exception {
        mockMvc.perform(post("/api/alertes/generate/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER_RH")
    void generateAlertes_returns5xx_whenPassationNotFound() throws Exception {
        mockMvc.perform(post("/api/alertes/generate/9999"))
                .andExpect(status().is5xxServerError());
    }
}
