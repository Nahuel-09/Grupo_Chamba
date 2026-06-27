package com.grupochamba.sz53.config;

import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.repositories.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepositoryJPA rolRepository;
    private final UsuarioRepositoryJPA usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepositoryJPA rolRepository,
            UsuarioRepositoryJPA usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Rol admin = crearRolSiNoExiste("ROLE_ADMIN");
        Rol stock = crearRolSiNoExiste("ROLE_STOCK");
        Rol vendedor = crearRolSiNoExiste("ROLE_VENDEDOR");

        crearUsuarioSiNoExiste("admin", "admin123", admin);
        crearUsuarioSiNoExiste("stock", "stock123", stock);
        crearUsuarioSiNoExiste("vendedor", "vendedor123", vendedor);
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }

    private void crearUsuarioSiNoExiste(String username, String passwordPlano, Rol rol) {

        if (usuarioRepository.findByUsername(username).isEmpty()) {

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(passwordPlano));
            usuario.setActivo(true);
            usuario.setRol(rol);

            usuarioRepository.save(usuario);
        }
    }
}