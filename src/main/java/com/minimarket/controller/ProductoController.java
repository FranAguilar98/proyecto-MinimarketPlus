package com.minimarket.controller;

import com.minimarket.dto.ProductoRequestDTO;
import com.minimarket.dto.ProductoResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import com.minimarket.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','EMPLEADO','CAJERO','ADMINISTRADOR')")
    public List<ProductoResponseDTO> listarProductos() {
        return productoService.findAll().stream()
                .map(ProductoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','EMPLEADO','CAJERO','ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        return (producto != null) ? ResponseEntity.ok(ProductoResponseDTO.fromEntity(producto)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> guardarProducto(
        @Valid @RequestBody ProductoRequestDTO dto){
            Producto guardado = productoService.save(mapearDesdeDTO(new Producto(), dto));
            return ResponseEntity.ok(ProductoResponseDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        Producto actualizado = productoService.save(mapearDesdeDTO(productoExistente, dto));
        return ResponseEntity.ok(ProductoResponseDTO.fromEntity(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Producto mapearDesdeDTO(Producto producto, ProductoRequestDTO dto){
        Categoria categoria = categoriaService.findById(dto.getCategoriaId());
        if(categoria == null){
            throw new IllegalArgumentException(
                "Categoria no encontrada: " + dto.getCategoriaId());
        }
        producto.setNombre(dto.getNombreSanitizado());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoria);
        return producto;
    }
}