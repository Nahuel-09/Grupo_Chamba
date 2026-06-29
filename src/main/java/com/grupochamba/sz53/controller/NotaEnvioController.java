package com.grupochamba.sz53.controller;

import java.time.LocalDate;
import java.util.*;
import jakarta.servlet.http.HttpSession;
import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.services.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notas")
public class NotaEnvioController {

    private final NotaEnvioService notaEnvioService;
    private final ProductoService productoService;

    public NotaEnvioController(
            NotaEnvioService notaEnvioService,
            ProductoService productoService) {
        this.notaEnvioService = notaEnvioService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "Notas de Envío");
        model.addAttribute("notas", notaEnvioService.listar());
        return "notas/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, HttpSession session) {
        NotaEnvio nota = obtenerNotaTemporal(session);
        List<DetalleNotaEnvio> detalles = obtenerDetalles(session);

        model.addAttribute("title", "Nueva Nota de Envío");
        model.addAttribute("nota", nota);
        model.addAttribute("detalles", detalles);
        model.addAttribute("productos", productoService.lista()); 

        return "notas/form";
    }

    @PostMapping("/detalle/agregar")
    public String agregarDetalleTemporal(
            @ModelAttribute NotaEnvio nota,
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) Double precio,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        nota.setVerificado(0);
        nota.setProcesado(0);
        session.setAttribute("notaTemporal", nota);

        if (productoId == null || cantidad == null || precio == null) {
            redirectAttributes.addFlashAttribute("error", "Todos los campos del detalle son obligatorios.");
            return "redirect:/notas/nueva";
        }

        if (cantidad <= 0 || precio <= 0) {
            redirectAttributes.addFlashAttribute("error", "La cantidad y el precio deben ser mayores a cero.");
            return "redirect:/notas/nueva";
        }

        Producto producto = productoService.buscarPorId(productoId);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "El producto seleccionado no existe.");
            return "redirect:/notas/nueva";
        }

        DetalleNotaEnvio detalle = new DetalleNotaEnvio();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);

        List<DetalleNotaEnvio> detalles = obtenerDetalles(session);
        detalles.add(detalle);

        session.setAttribute("detallesNota", detalles);
        redirectAttributes.addFlashAttribute("success", "Artículo agregado al detalle temporal.");

        return "redirect:/notas/nueva";
    }

    @GetMapping("/detalle/eliminar/{index}")
    public String eliminarDetalleTemporal(
            @PathVariable Integer index,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        List<DetalleNotaEnvio> detalles = obtenerDetalles(session);

        if (index >= 0 && index < detalles.size()) {
            detalles.remove(index.intValue());
            redirectAttributes.addFlashAttribute("success", "Artículo removido.");
        }

        session.setAttribute("detallesNota", detalles);
        return "redirect:/notas/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute NotaEnvio nota,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        List<DetalleNotaEnvio> detalles = obtenerDetalles(session);

        if (detalles.isEmpty()) {
            session.setAttribute("notaTemporal", nota);
            redirectAttributes.addFlashAttribute("error", "No puede registrar una Nota de Envío sin artículos.");
            return "redirect:/notas/nueva";
        }

        try {
            nota.setVerificado(0);
            nota.setProcesado(0);

            for (DetalleNotaEnvio detalle : detalles) {
                detalle.setNotaEnvio(nota);
            }

            nota.setDetalles(detalles);
            NotaEnvio notaGuardada = notaEnvioService.guardar(nota);

            session.removeAttribute("notaTemporal");
            session.removeAttribute("detallesNota");

            redirectAttributes.addFlashAttribute("success", "Nota de Envío registrada como borrador.");
            return "redirect:/notas/ver/" + notaGuardada.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el documento: " + e.getMessage());
            return "redirect:/notas/nueva";
        }
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        NotaEnvio nota = notaEnvioService.buscarPorId(id);

        if (nota == null) {
            redirectAttributes.addFlashAttribute("error", "La nota de envío solicitada no existe.");
            return "redirect:/notas";
        }

        model.addAttribute("title", "Nota de Envío #" + id);
        model.addAttribute("nota", nota);
        model.addAttribute("productos", productoService.lista());
        
        
        model.addAttribute("esNueva", notaEnvioService.esNuevaEditable(nota));
        model.addAttribute("esVerificada", notaEnvioService.esVerificadaNoProcesada(nota));

        return "notas/ver";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        NotaEnvio nota = notaEnvioService.buscarPorId(id);

        if (nota == null) {
            redirectAttributes.addFlashAttribute("error", "La nota no existe.");
            return "redirect:/notas";
        }

        if (!notaEnvioService.esNuevaEditable(nota)) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden editar cabeceras de notas en estado borrador (Nuevas).");
            return "redirect:/notas/ver/" + id;
        }

        model.addAttribute("title", "Editar Nota de Envío");
        model.addAttribute("nota", nota);
        return "notas/editar";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(
            @PathVariable Long id,
            @ModelAttribute NotaEnvio nota,
            RedirectAttributes redirectAttributes) {
        try {
            notaEnvioService.actualizarCabecera(id, nota);
            redirectAttributes.addFlashAttribute("success", "Cabecera actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notas/ver/" + id;
    }

    @PostMapping("/detalle/agregar-guardado/{id}")
    public String agregarDetalleGuardado(
            @PathVariable Long id,
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false) Double precio,
            RedirectAttributes redirectAttributes) {

        try {
            Producto producto = productoService.buscarPorId(productoId);
            DetalleNotaEnvio detalle = new DetalleNotaEnvio();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(precio);

            notaEnvioService.agregarDetalleGuardado(id, detalle);
            redirectAttributes.addFlashAttribute("success", "Producto añadido a la nota.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/notas/ver/" + id;
    }

    @GetMapping("/detalle/eliminar-guardado/{notaId}/{detalleId}")
    public String eliminarDetalleGuardado(
            @PathVariable Long notaId,
            @PathVariable Long detalleId,
            RedirectAttributes redirectAttributes) {

        try {
            notaEnvioService.eliminarDetalleGuardado(notaId, detalleId);
            redirectAttributes.addFlashAttribute("success", "Ítem eliminado de la nota.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/notas/ver/" + notaId;
    }
    
    @GetMapping("/eliminar/{id}") 
    public String eliminarNota(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notaEnvioService.eliminarNota(id);
            redirectAttributes.addFlashAttribute("success", "Nota de envío eliminada permanentemente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/notas/ver/" + id;
        }
        return "redirect:/notas";
    }

    @SuppressWarnings("unchecked")
    private List<DetalleNotaEnvio> obtenerDetalles(HttpSession session) {
        List<DetalleNotaEnvio> detalles = (List<DetalleNotaEnvio>) session.getAttribute("detallesNota");
        if (detalles == null) {
            detalles = new ArrayList<>();
            session.setAttribute("detallesNota", detalles);
        }
        return detalles;
    }

    private NotaEnvio obtenerNotaTemporal(HttpSession session) {
        NotaEnvio nota = (NotaEnvio) session.getAttribute("notaTemporal");
        if (nota == null) {
            nota = new NotaEnvio();
            nota.setFecha(LocalDate.now());
            nota.setVerificado(0);
            nota.setProcesado(0);
            session.setAttribute("notaTemporal", nota);
        }
        return nota;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STOCK')")
    @PostMapping("/verificar/{id}")
    public String verificar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notaEnvioService.verificar(id);
            redirectAttributes.addFlashAttribute("success", "Nota verificada. Lista para el impacto definitivo de stock.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notas/ver/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STOCK')")
    @PostMapping("/desverificar/{id}")
    public String desverificar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notaEnvioService.desverificar(id);
            redirectAttributes.addFlashAttribute("success", "Se ha cancelado la verificación de la nota.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notas/ver/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STOCK')")
    @PostMapping("/procesar/{id}")
    public String procesar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            notaEnvioService.procesar(id);
            redirectAttributes.addFlashAttribute("success", "Nota procesada con éxito. El stock fue incrementado en el inventario.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/notas/ver/" + id;
    }
}