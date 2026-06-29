package com.minimarket.dto;

import com.minimarket.entity.Producto;

public class ProductoResponseDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private Long categoriaId;
    private String categoriaNombre;

    public ProductoResponseDTO(){
    }

    public static ProductoResponseDTO fromEntity(Producto producto){
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.id = producto.getId();
        dto.nombre = producto.getNombre();
        dto.precio = producto.getPrecio();
        dto.stock = producto.getStock();
        if(producto.getCategoria() != null){
            dto.categoriaId = producto.getCategoria().getId();
            dto.categoriaNombre = producto.getCategoria().getNombre();
        }
        return dto;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Double getPrecio(){
        return precio;
    }

    public void setPrecio(Double precio){
        this.precio = precio;
    }

    public Integer getStock(){
        return stock;
    }

    public void setStock(Integer stock){
        this.stock = stock;
    }

    public Long getCategoriaId(){
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId){
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre(){
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre){
        this.categoriaNombre = categoriaNombre;
    }
}
