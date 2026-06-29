package com.minimarket.config;


import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializerc implements ApplicationRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        crearRolSiNoExiste("CLIENTE");
        crearRolSiNoExiste("EMPLEADO");
        crearRolSiNoExiste("ADMINISTRADOR");
        crearRolSiNoExiste("CAJERO");

        crearUsuarioSiNoExiste("cliente", "cliente123", Set.of("CLIENTE"));
        crearUsuarioSiNoExiste("empleado", "empleado123", Set.of("EMPLEADO"));
        crearUsuarioSiNoExiste("administrador", "admin123", Set.of("ADMINISTRADOR"));
        crearUsuarioSiNoExiste("cajero","cajero123", Set.of("CAJERO"));
    }

    private void crearRolSiNoExiste(String nombre) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
            System.out.println("Rol creado: " + nombre);
        }
    }

    private void crearUsuarioSiNoExiste(String username, String password, Set<String> roles) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRoles(roles.stream()
                    .map(rolNombre -> rolRepository.findByNombre(rolNombre).orElseThrow())
                    .collect(java.util.stream.Collectors.toSet()));
            usuarioRepository.save(usuario);
            System.out.println("Usuario creado: " + username);
        }
    }

}
