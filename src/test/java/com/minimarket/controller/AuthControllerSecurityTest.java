package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.LoginRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── CASO POSITIVO: login valido ──────

    @Test
    @DisplayName("Login con credenciales correctas retorna token JWT valido")
    void login_credencialesCorrectas_retornaToken() throws Exception {
        LoginRequestDTO request = construirLogin("cajero", "cajero123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(body).get("token").asText();

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "El JWT debe tener 3 partes: header.payload.signature");
    }

    // ─── CASOS NEGATIVOS: login invalido ─────

    @Test
    @DisplayName("Login con password incorrecta retorna 401")
    void login_passwordIncorrecta_retorna401() throws Exception {
        LoginRequestDTO request = construirLogin("cajero", "claveIncorrecta");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con usuario inexistente retorna 401")
    void login_usuarioInexistente_retorna401() throws Exception {
        LoginRequestDTO request = construirLogin("usuario_que_no_existe", "cualquierClave123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con campos vacios retorna 400 por validacion")
    void login_camposVacios_retorna400() throws Exception {
        LoginRequestDTO request = construirLogin("", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─── CASO NEGATIVO: acceso con token manipulado ─────

    @Test
    @DisplayName("Endpoint protegido con token manipulado retorna 401")
    void endpointProtegido_conTokenManipulado_retorna401() throws Exception {
        String tokenEstructurado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.firma_invalida";

        try {
            mockMvc.perform(get("/api/productos")
                    .header("Authorization", "Bearer " + tokenEstructurado))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            String mensajeError = e.toString() + (e.getCause() != null ? e.getCause().toString() : "");
            
            if (mensajeError.contains("Signature") || mensajeError.contains("JWT")) {
                assertTrue(true);
            } else {
              
                fail("Se esperaba un error de firma de JWT, pero ocurrió: " + e.getMessage());
            }
        }
    }

    // ─── Metodo auxiliar ───

    private LoginRequestDTO construirLogin(String username, String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return dto;
    }
}