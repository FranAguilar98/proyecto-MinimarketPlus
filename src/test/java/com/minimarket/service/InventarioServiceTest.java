package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto productoPrueba;
    private Inventario inventarioPrueba;

    @BeforeEach
    void configurarDatosDePrueba() {
        productoPrueba = new Producto();
        productoPrueba.setId(100L);
        productoPrueba.setNombre("Pan de molde");
        productoPrueba.setPrecio(2000.0);
        productoPrueba.setStock(20);

        inventarioPrueba = new Inventario();
        inventarioPrueba.setId(1L);
        inventarioPrueba.setProducto(productoPrueba);
        inventarioPrueba.setCantidad(15);
        inventarioPrueba.setTipoMovimiento("Entrada");
        inventarioPrueba.setFechaMovimiento(new Date());
    }

    @Test
    @DisplayName("Debe guardar correctamente un movimiento de inventario valido")
    void save_movimientoValido_debeGuardarse() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioPrueba);

        Inventario resultado = inventarioService.save(inventarioPrueba);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertEquals(15, resultado.getCantidad());
        verify(inventarioRepository, times(1)).save(inventarioPrueba);
    }

    @Test
    @DisplayName("El tipoMovimiento no debe ser nulo")
    void inventario_tipoMovimientoNulo_noDebeSerValido() {
        Inventario inventarioInvalido = new Inventario();
        inventarioInvalido.setProducto(productoPrueba);
        inventarioInvalido.setCantidad(5);
        inventarioInvalido.setTipoMovimiento(null);
        inventarioInvalido.setFechaMovimiento(new Date());

        assertNull(inventarioInvalido.getTipoMovimiento());
        assertTrue(esMovimientoInvalido(inventarioInvalido));
    }

    @Test
    @DisplayName("El tipoMovimiento no debe estar vacio")
    void inventario_tipoMovimientoVacio_noDebeSerValido() {
        Inventario inventarioInvalido = new Inventario();
        inventarioInvalido.setProducto(productoPrueba);
        inventarioInvalido.setCantidad(5);
        inventarioInvalido.setTipoMovimiento("");
        inventarioInvalido.setFechaMovimiento(new Date());

        assertTrue(esMovimientoInvalido(inventarioInvalido));
    }

    @Test
    @DisplayName("La cantidad no debe ser nula")
    void inventario_cantidadNula_noDebeSerValido() {
        Inventario inventarioInvalido = new Inventario();
        inventarioInvalido.setProducto(productoPrueba);
        inventarioInvalido.setCantidad(null);
        inventarioInvalido.setTipoMovimiento("Salida");
        inventarioInvalido.setFechaMovimiento(new Date());

        assertTrue(esMovimientoInvalido(inventarioInvalido));
    }

    @Test
    @DisplayName("El inventario debe estar asociado al producto correcto")
    void inventario_debeAsociarProductoCorrecto() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioPrueba);

        Inventario resultado = inventarioService.save(inventarioPrueba);

        assertNotNull(resultado.getProducto());
        assertEquals(productoPrueba.getId(), resultado.getProducto().getId());
        assertEquals("Pan de molde", resultado.getProducto().getNombre());
    }

    @Test
    @DisplayName("findByProductoId debe retornar solo movimientos del producto solicitado")
    void findByProductoId_debeRetornarMovimientosDelProducto() {
        when(inventarioRepository.findByProductoId(100L))
                .thenReturn(java.util.List.of(inventarioPrueba));

        var resultado = inventarioService.findByProductoId(100L);

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getProducto().getId());
        verify(inventarioRepository, times(1)).findByProductoId(100L);
    }

    private boolean esMovimientoInvalido(Inventario inventario) {
        boolean tipoInvalido = inventario.getTipoMovimiento() == null
                || inventario.getTipoMovimiento().trim().isEmpty();
        boolean cantidadInvalida = inventario.getCantidad() == null;
        return tipoInvalido || cantidadInvalida;
    }

    @Test
@DisplayName("findAll debe retornar la lista de inventarios")
void findAll_debeRetornarListaDeInventarios() {
    when(inventarioRepository.findAll()).thenReturn(java.util.List.of(inventarioPrueba));

    var resultado = inventarioService.findAll();

    assertEquals(1, resultado.size());
    verify(inventarioRepository, times(1)).findAll();
}

@Test
@DisplayName("findById debe retornar el inventario cuando existe")
void findById_conIdExistente_debeRetornarInventario() {
    when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioPrueba));

    Inventario resultado = inventarioService.findById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
}

@Test
@DisplayName("findById debe retornar null cuando el inventario no existe")
void findById_conIdInexistente_debeRetornarNull() {
    when(inventarioRepository.findById(999L)).thenReturn(Optional.empty());

    Inventario resultado = inventarioService.findById(999L);

    assertNull(resultado);
}

@Test
@DisplayName("deleteById debe invocar la eliminacion en el repositorio")
void deleteById_debeEliminarInventario() {
    inventarioService.deleteById(1L);

    verify(inventarioRepository, times(1)).deleteById(1L);
}
}