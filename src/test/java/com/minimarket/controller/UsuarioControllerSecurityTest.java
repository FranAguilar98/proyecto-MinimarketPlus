package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.UsuarioRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── CASO POSITIVO: ADMINISTRADOR si puede crear usuarios ───

    @Test
    @DisplayName("ADMINISTRADOR puede crear un usuario con roles validos")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_puedeCrearUsuario() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // ─── CASOS NEGATIVOS DE ROL───────

    @Test
    @DisplayName("EMPLEADO no puede crear usuarios (403)")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_noPuedeCrearUsuario() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CAJERO no puede listar usuarios (403)")
    @WithMockUser(username = "cajero_test", roles = {"CAJERO"})
    void cajero_noPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticacion no se puede crear usuarios (401)")
    void sinAutenticacion_noPuedeCrearUsuario() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ─── CASOS NEGATIVOS DE VALIDACION ──

    @Test
    @DisplayName("No se puede crear un usuario con el set de roles vacio")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_noPuedeCrearUsuario_conRolesVacios() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();
        dto.setRoles(Set.of()); // roles vacio: viola @NotEmpty del DTO

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("No se puede crear un usuario con username vacio")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_noPuedeCrearUsuario_conUsernameVacio() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();
        dto.setUsername(""); 

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("No se puede crear un usuario con password muy corta")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_noPuedeCrearUsuario_conPasswordCorta() throws Exception {
        UsuarioRequestDTO dto = construirUsuarioValido();
        dto.setPassword("123"); 

        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ─── Metodo auxiliar ─────

    private UsuarioRequestDTO construirUsuarioValido() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setUsername("nuevo_empleado");
        dto.setPassword("clave123");
        dto.setRoles(Set.of("EMPLEADO"));
        return dto;
    }
}