package com.minimarket.dto;

import com.minimarket.entity.Usuario;

import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioResponseDTO{

    private Long id;
    private String username;
    private Set<String> roles;

    public UsuarioResponseDTO(){
    }

    public UsuarioResponseDTO(Long id, String username, Set<String> roles){
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario){
        Set<String> nombresRoles = usuario.getRoles() == null ? Set.of()
                : usuario.getRoles().stream()
                    .map(r -> r.getNombre())
                    .collect(Collectors.toSet());
        return new UsuarioResponseDTO(usuario.getId(), usuario.getUsername(), nombresRoles);
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public Set<String> getRoles(){
        return roles;
    }

    public void setRoles(Set<String> roles){
        this.roles = roles;
    }
}