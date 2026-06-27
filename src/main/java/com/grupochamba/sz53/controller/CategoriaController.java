package com.grupochamba.sz53.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.grupochamba.sz53.model.Categoria;
import com.grupochamba.sz53.services.CategoriaService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService catServ;

    public CategoriaController(CategoriaService catServ) {
        this.catServ = catServ;
    }

    @GetMapping("/")
    public String mostrarCategoria(Model model) {
        model.addAttribute("categorias", catServ.listar());
        model.addAttribute("title", "Lista Categorias");
        return "categoria/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Categoria cat = new Categoria();
        model.addAttribute("modoedicion", false);
        model.addAttribute("categoria", cat);
        model.addAttribute("title", "Nueva Categoria");
        return "categoria/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, Model model) {
        try {
            catServ.crear(categoria);
            return "redirect:/categorias/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modoedicion", false);
            model.addAttribute("categoria", categoria);
            model.addAttribute("title", "Nueva Categoria");
            return "categoria/form";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id) {
        catServ.eliminar(id);
        return "redirect:/categorias/";
    }

    @GetMapping("/editar/{id}")
    private String Editar(@PathVariable int id, Model model) {
        Categoria cat = catServ.buscarpoid(id);
        model.addAttribute("modoedicion", true);
        model.addAttribute("categoria", cat);
        model.addAttribute("title", "Editar Categoria");
        return "categoria/form";
    }

    @PostMapping("/actualizar")
    private String actualizar(@ModelAttribute Categoria categoria, Model model) {
        try {
            catServ.actualizar(categoria);
            return "redirect:/categorias/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modoedicion", true);
            model.addAttribute("categoria", categoria);
            model.addAttribute("title", "Nueva Categoria");
            return "categoria/form";
        }

    }
}