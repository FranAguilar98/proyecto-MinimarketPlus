package com.minimarket.service.impl;

import com.minimarket.dto.UsuarioRequestDTO;
import com.minimarket.dto.UsuarioResponseDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.RolService;
import com.minimarket.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioResponseDTO crearDesdeDTO(UsuarioRequestDTO dto){
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRoles(resolverRoles(dto.getRoles()));
        return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
    }

    @Override
    public Optional<UsuarioResponseDTO> actualizarDesdeDTO(Long id, UsuarioRequestDTO dto){
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setUsername(dto.getUsername());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuario.setRoles(resolverRoles(dto.getRoles()));
            return UsuarioResponseDTO.fromEntity(usuarioRepository.save(usuario));
        });
    }

    private Set<Rol> resolverRoles(Set<String> nombresRoles){
        Set<Rol> roles = new HashSet<>();
        for(String nombre : nombresRoles){
            Rol rol = rolService.findByNombre(nombre)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Rol no encontrado: " + nombre));
            roles.add(rol);
        }
        return roles;
    }
}
