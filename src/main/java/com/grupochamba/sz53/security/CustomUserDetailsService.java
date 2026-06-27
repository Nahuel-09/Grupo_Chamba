package com.grupochamba.sz53.security;

import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.repositories.*;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositoryJPA usuarioRepositoryJPA;

    public CustomUserDetailsService(UsuarioRepositoryJPA usuarioRepositoryJPA) {
        this.usuarioRepositoryJPA = usuarioRepositoryJPA;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositoryJPA.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (usuario.getRol() == null || usuario.getRol().getNombre() == null) {
            throw new UsernameNotFoundException("El usuario no tiene rol asignado: " + username);
        }

        String rolFormateado = usuario.getRol().getNombre().toUpperCase();
        if (!rolFormateado.startsWith("ROLE_")) {
            rolFormateado = "ROLE_" + rolFormateado;
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getUsername())
                .password(usuario.getPassword()).disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .authorities(rolFormateado)
                .build();
    }
}