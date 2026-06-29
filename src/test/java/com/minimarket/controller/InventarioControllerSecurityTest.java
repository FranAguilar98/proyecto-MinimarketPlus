package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
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
class InventarioControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Producto productoExistente;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria Test " + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        productoExistente = new Producto();
        productoExistente.setNombre("Producto Test");
        productoExistente.setPrecio(1000.0);
        productoExistente.setStock(50);
        productoExistente.setCategoria(categoria);
        productoExistente = productoRepository.save(productoExistente);
    }

    // ─── CASOS POSITIVOS: roles con permiso si pueden registrar ─

    @Test
    @DisplayName("EMPLEADO puede registrar un movimiento de inventario")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_puedeRegistrarMovimiento() throws Exception {
        Inventario inventario = construirMovimientoValido();

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ADMINISTRADOR puede registrar un movimiento de inventario")
    @WithMockUser(username = "admin_test", roles = {"ADMINISTRADOR"})
    void administrador_puedeRegistrarMovimiento() throws Exception {
        Inventario inventario = construirMovimientoValido();

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    // ─── CASOS NEGATIVOS: sin permiso, no puede registrar ───────

    @Test
    @DisplayName("CLIENTE no puede registrar un movimiento de inventario (403)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_noPuedeRegistrarMovimiento() throws Exception {
        Inventario inventario = construirMovimientoValido();

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticacion no se puede registrar un movimiento (401)")
    void sinAutenticacion_noPuedeRegistrarMovimiento() throws Exception {
        Inventario inventario = construirMovimientoValido();

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Solo ADMINISTRADOR puede eliminar un movimiento de inventario")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void empleado_noPuedeEliminarMovimiento() throws Exception {
        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isForbidden());
    }

    // ─── CASO NEGATIVO DE NEGOCIO─

    @Test
    @DisplayName("No se puede registrar un movimiento sin producto asociado")
    @WithMockUser(username = "empleado_test", roles = {"EMPLEADO"})
    void noSePuedeRegistrarMovimiento_sinProductoAsociado() throws Exception {
        Inventario inventarioSinProducto = new Inventario();
        inventarioSinProducto.setCantidad(10);
        inventarioSinProducto.setTipoMovimiento("Entrada");
        inventarioSinProducto.setFechaMovimiento(new Date());

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioSinProducto)))
                .andExpect(status().is4xxClientError());
    }

    // ─── Lectura permitida a todos los roles autenticados ───────

    @Test
    @DisplayName("CLIENTE puede listar movimientos (solo lectura)")
    @WithMockUser(username = "cliente_test", roles = {"CLIENTE"})
    void cliente_puedeListarMovimientos() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    // ─── Metodo auxiliar ─────

    private Inventario construirMovimientoValido() {
        Inventario inventario = new Inventario();
        inventario.setCantidad(20);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
        inventario.setProducto(productoExistente);
        return inventario;
    }
}