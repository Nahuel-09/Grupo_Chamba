package com.grupochamba.sz53.controller;

import java.util.List;
import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService servicio;
    private final CategoriaService catServ;

    public ProductoController(ProductoService servicio, CategoriaService catServ ) {
        this.servicio = servicio;
        this.catServ = catServ;
    }

    @GetMapping("/")
    public String listar(Model model) {
        List<Producto> datos = servicio.lista();
        model.addAttribute("datos", datos);
        model.addAttribute("title", "Lista de Productos");
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto dato = new Producto();
        List<Categoria> categorias = catServ.listar();
        model.addAttribute("modoedicion", false);
        model.addAttribute("dato", dato);
        model.addAttribute("categorias", categorias);

        model.addAttribute("title", "Nueva Producto");
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto dato, Model model) {
        try {
            servicio.guardar(dato);
            return "redirect:/productos/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modoedicion", false);
            model.addAttribute("dato", dato);
            model.addAttribute("title", "Nueva Producto");
            return "productos/form";
        }
        
        

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        
        servicio.eliminar(id);
        return "redirect:/productos/";
    }

    @GetMapping("/editar/{id}")
    private String Editar(@PathVariable Long id, Model model) {
        Producto dato = servicio.buscarPorId(id);
        List<Categoria> categorias = catServ.listar();
        model.addAttribute("modoedicion", true);
        model.addAttribute("dato", dato);
        model.addAttribute("categorias", categorias);
        model.addAttribute("title", "Editar Producto");
        return "productos/form";
    }

    @PostMapping("/actualizar")
    private String actualizar(@ModelAttribute Producto dato, Model model) {
        try {
            servicio.guardar(dato);
            return "redirect:/productos/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modoedicion", true);
            model.addAttribute("dato", dato);
            model.addAttribute("title", "Nueva Producto");
            return "productos/form";
        }

    }
}