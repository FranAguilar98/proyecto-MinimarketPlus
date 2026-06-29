package com.minimarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UsuarioRequestDTO {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$",
            message = "El usuario solo puede contener letras, numeros, '.', '_' o '-'")
    private String username;

    @NotBlank(message = "La contrasenia es obligatoria")
    @Size(min = 6, max = 100, message = "La contrasenia debe tener entre 6 y 100 caracteres")
    private String password;

    @NotEmpty(message = "Debe asignar al menos un rol")
    private Set<String> roles;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Set<String> getRoles(){
        return roles;
    }

    public void setRoles(Set<String> roles){
        this.roles = roles;
    }
}
