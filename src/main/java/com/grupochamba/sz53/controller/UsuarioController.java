package com.grupochamba.sz53.controller;

import com.grupochamba.sz53.repositories.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController {

    private final UsuarioRepositoryJPA usuarioRepository;

    public UsuarioController(UsuarioRepositoryJPA usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {

        model.addAttribute("title", "Usuarios del sistema");
        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "usuarios/lista";
    }
}
