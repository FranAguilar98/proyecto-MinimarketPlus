package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.ProductoRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ─── CASOS POSITIVOS: ADMINISTRADOR si puede modificar ──────

    @Test
    @DisplayName("ADMINISTRADOR puede crear un producto")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_puedeCrearProducto() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    // ─── CASOS NEGATIVOS: otros roles no pueden modificar ───────

    @Test
    @DisplayName("EMPLEADO no puede crear un producto (403)")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_noPuedeCrearProducto() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CLIENTE no puede crear un producto (403)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_noPuedeCrearProducto() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticacion no se puede crear un producto (401)")
    void sinAutenticacion_noPuedeCrearProducto() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("EMPLEADO no puede actualizar un producto (403)")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_noPuedeActualizarProducto() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    // ─── CASO NEGATIVO DE NEGOCIO: precio invalido ──

    @Test
    @DisplayName("ADMINISTRADOR no puede crear producto con precio negativo")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_noPuedeCrearProductoConPrecioNegativo() throws Exception {
        ProductoRequestDTO dto = construirProductoValido();
        dto.setPrecio(-500.0); 

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ─── Acceso de lectura si permitido a todos los roles ───────

    @Test
    @DisplayName("CLIENTE puede listar productos (solo lectura)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_puedeListarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    // ─── Metodo auxiliar ────

    private ProductoRequestDTO construirProductoValido() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Leche Entera 1L");
        dto.setPrecio(1200.0);
        dto.setStock(15);
        dto.setCategoriaId(1L);
        return dto;
    }
}