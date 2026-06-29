package com.minimarket.service;

import com.minimarket.dto.UsuarioRequestDTO;
import com.minimarket.dto.UsuarioResponseDTO;
import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    UsuarioResponseDTO crearDesdeDTO(UsuarioRequestDTO dto);
    Optional<UsuarioResponseDTO> actualizarDesdeDTO(Long id, UsuarioRequestDTO dto);
}