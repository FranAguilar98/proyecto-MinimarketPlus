package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuarioPrueba;
    private Producto productoPrueba;

    @BeforeEach
    void configurarDatosDePrueba() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setUsername("Juanito");

        productoPrueba = new Producto();
        productoPrueba.setId(100L);
        productoPrueba.setNombre("Jugo Watts");
        productoPrueba.setPrecio(1500.0);
        productoPrueba.setStock(10);
    }

    @Test
    @DisplayName("Debe agregar el producto al carrito cuando hay stock suficiente")
    void agregarProducto_conStockSuficiente_debeCrearCarrito() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoPrueba));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 100L, 3);

        assertNotNull(resultado);
        assertEquals(3, resultado.getCantidad());
        assertEquals(productoPrueba, resultado.getProducto());
        assertEquals(7, productoPrueba.getStock());

        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el stock es insuficiente")
    void agregarProducto_sinStockSuficiente_debeLanzarExcepcion() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoPrueba));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        IllegalStateException excepcion = assertThrows(
                IllegalStateException.class,
                () -> carritoService.agregarProducto(1L, 100L, 50)
        );

        assertTrue(excepcion.getMessage().contains("Stock insuficiente"));

        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando la cantidad es cero o negativa")
    void agregarProducto_conCantidadInvalida_debeLanzarExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 100L, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 100L, -5)
        );

        verify(productoRepository, never()).findById(any());
    }

    @Test
    @DisplayName("El carrito creado debe estar asociado al usuario correcto")
    void agregarProducto_debeAsociarUsuarioCorrecto() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoPrueba));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 100L, 2);

        assertNotNull(resultado.getUsuario());
        assertEquals(usuarioPrueba.getId(), resultado.getUsuario().getId());
        assertEquals("Juanito", resultado.getUsuario().getUsername());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el producto no existe")
    void agregarProducto_conProductoInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 999L, 1)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando el usuario no existe")
    void agregarProducto_conUsuarioInexistente_debeLanzarExcepcion() {
        when(productoRepository.findById(100L)).thenReturn(Optional.of(productoPrueba));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(999L, 100L, 1)
        );
    }

    @Test
@DisplayName("findAll debe retornar la lista de carritos")
void findAll_debeRetornarListaDeCarritos() {
    Carrito carrito = new Carrito();
    carrito.setId(1L);
    when(carritoRepository.findAll()).thenReturn(java.util.List.of(carrito));

    var resultado = carritoService.findAll();

    assertEquals(1, resultado.size());
    verify(carritoRepository, times(1)).findAll();
}

@Test
@DisplayName("findById debe retornar el carrito cuando existe")
void findById_conIdExistente_debeRetornarCarrito() {
    Carrito carrito = new Carrito();
    carrito.setId(1L);
    when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

    Carrito resultado = carritoService.findById(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());
}

@Test
@DisplayName("findById debe retornar null cuando el carrito no existe")
void findById_conIdInexistente_debeRetornarNull() {
    when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

    Carrito resultado = carritoService.findById(999L);

    assertNull(resultado);
}

@Test
@DisplayName("save debe guardar el carrito correctamente")
void save_debeGuardarCarrito() {
    Carrito carrito = new Carrito();
    carrito.setCantidad(5);
    when(carritoRepository.save(carrito)).thenReturn(carrito);

    Carrito resultado = carritoService.save(carrito);

    assertNotNull(resultado);
    assertEquals(5, resultado.getCantidad());
    verify(carritoRepository, times(1)).save(carrito);
}

@Test
@DisplayName("deleteById debe invocar la eliminacion en el repositorio")
void deleteById_debeEliminarCarrito() {
    carritoService.deleteById(1L);

    verify(carritoRepository, times(1)).deleteById(1L);
}

@Test
@DisplayName("findByUsuarioId debe retornar los carritos del usuario")
void findByUsuarioId_debeRetornarCarritosDelUsuario() {
    Carrito carrito = new Carrito();
    carrito.setId(1L);
    when(carritoRepository.findByUsuarioId(1L)).thenReturn(java.util.List.of(carrito));

    var resultado = carritoService.findByUsuarioId(1L);

    assertEquals(1, resultado.size());
    verify(carritoRepository, times(1)).findByUsuarioId(1L);
}
}