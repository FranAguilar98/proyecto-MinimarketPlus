package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class VentaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioExistente = new Usuario();
        usuarioExistente.setUsername("usuario_venta_test_" + System.nanoTime());
        usuarioExistente.setPassword("password123");
        usuarioExistente = usuarioRepository.save(usuarioExistente);
    }

    // ─── CASOS POSITIVOS: CAJERO si puede generar ventas ────────

    @Test
    @DisplayName("CAJERO puede generar una venta")
    @WithMockUser(username = "cajero_test", roles = {"CAJERO"})
    void cajero_puedeGenerarVenta() throws Exception {
        Venta venta = construirVentaValida();

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk());
    }

    // ─── CASOS NEGATIVOS: otros roles NO pueden generar ventas ──

    @Test
    @DisplayName("ADMINISTRADOR no puede generar una venta (403) - solo CAJERO puede")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_noPuedeGenerarVenta() throws Exception {
        Venta venta = construirVentaValida();

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CLIENTE no puede generar una venta (403)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_noPuedeGenerarVenta() throws Exception {
        Venta venta = construirVentaValida();

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("EMPLEADO no puede generar una venta (403)")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_noPuedeGenerarVenta() throws Exception {
        Venta venta = construirVentaValida();

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticacion no se puede generar una venta (401)")
    void sinAutenticacion_noPuedeGenerarVenta() throws Exception {
        Venta venta = construirVentaValida();

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isForbidden());
    }

    // ─── Lectura permitida a todos los roles ───────

    @Test
    @DisplayName("CLIENTE puede listar sus ventas (solo lectura)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_puedeListarVentas() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    // ─── Metodo auxiliar ──

    private Venta construirVentaValida() {
        Venta venta = new Venta();
        venta.setFecha(new Date());
        venta.setUsuario(usuarioExistente);
        return venta;
    }
}