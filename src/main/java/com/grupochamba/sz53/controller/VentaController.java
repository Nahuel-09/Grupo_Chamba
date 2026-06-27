package com.grupochamba.sz53.controller;

import jakarta.servlet.http.HttpSession;
import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public VentaController(VentaService ventaService,
                           ClienteService clienteService,
                           ProductoService productoService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "Historial de Ventas");
        model.addAttribute("ventas", ventaService.listar());
        return "ventas/lista"; 
    }

    @GetMapping("/formulario")
    public String formulario(Model model, HttpSession session) {
        model.addAttribute("title", "Nueva Operación de Venta");
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("detalleVenta", obtenerDetalle(session));
        return "ventas/form";
    }
    
    @GetMapping("/nueva")
    public String nueva(Model model, HttpSession session) {
        session.setAttribute("detalleVenta", new ArrayList<DetalleVenta>());


        model.addAttribute("title", "Nueva venta");
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("detalleVenta", obtenerDetalle(session));

        return "ventas/form";
    }

    @PostMapping("/agregar-detalle")
    public String agregarDetalle(@RequestParam Long productoId,
                                 @RequestParam Integer cantidad,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        Producto producto = productoService.buscarPorId(productoId);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "El producto seleccionado no existe.");
            return "redirect:/ventas/formulario";
        }

        if (cantidad == null || cantidad <= 0) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar una cantidad válida mayor a cero.");
            return "redirect:/ventas/formulario";
        }

        
        if (producto.getStock() == null || producto.getStock() < cantidad) {
            redirectAttributes.addFlashAttribute("error", "Stock insuficiente para " + producto.getNombre() + " (Disponibles: " + producto.getStock() + ")");
            return "redirect:/ventas/formulario";
        }

        List<DetalleVenta> detalleVenta = obtenerDetalle(session);

        
        for (DetalleVenta d : detalleVenta) {
            if (d.getProducto().getId().equals(productoId)) {
                int nuevaCantidad = d.getCantidad() + cantidad;
                if (producto.getStock() < nuevaCantidad) {
                    redirectAttributes.addFlashAttribute("error", "La cantidad acumulada supera al stock disponible.");
                    return "redirect:/ventas/formulario";
                }
                d.setCantidad(nuevaCantidad);
                
                session.setAttribute("detalleVenta", detalleVenta);
                return "redirect:/ventas/formulario";
            }
        }

        
        DetalleVenta detalle = new DetalleVenta(producto, cantidad, producto.getPrecio());
        
        double subtotal = cantidad * producto.getPrecio();
        detalle.setSubtotal(subtotal);
        detalle.setIva(subtotal * 0.10); 
        detalle.setGravada(subtotal - detalle.getIva());

        detalleVenta.add(detalle);
        session.setAttribute("detalleVenta", detalleVenta);

        return "redirect:/ventas/formulario";
    }

    @GetMapping("/quitar-detalle/{index}")
    public String quitarDetalle(@PathVariable int index, HttpSession session) {
        List<DetalleVenta> detalleVenta = obtenerDetalle(session);

        if (index >= 0 && index < detalleVenta.size()) {
            detalleVenta.remove(index);
        }

        session.setAttribute("detalleVenta", detalleVenta);
        return "redirect:/ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Long clienteId, HttpSession session,RedirectAttributes redirectAttributes) {

        Cliente cliente = clienteService.buscarPorId(clienteId);
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente válido.");
            return "redirect:/ventas/formulario";
        }

        List<DetalleVenta> detalleVenta = obtenerDetalle(session);
        if (detalleVenta.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No puede guardar una venta sin ítems en el detalle.");
            return "redirect:/ventas/formulario";
        }

        try {
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setProcesado(0); 

            
            for (DetalleVenta detalle : detalleVenta) {
                venta.agregarDetalle(detalle);
            }

            ventaService.guardar(venta);
            session.removeAttribute("detalleVenta"); 
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el documento: " + e.getMessage());
            return "redirect:/ventas/formulario";
        }

        return "redirect:/ventas";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Venta venta = ventaService.buscarPorId(id);

        if (venta == null) {
            redirectAttributes.addFlashAttribute("error", "La venta solicitada no existe.");
            return "redirect:/ventas";
        }

        model.addAttribute("title", "Comprobante de Venta #" + id);
        model.addAttribute("venta", venta);
        return "ventas/ver";
    }

    
    @PostMapping("/procesar/{id}")
    public String procesar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Venta venta = ventaService.buscarPorId(id);
            if (venta == null) {
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada.");
                return "redirect:/ventas";
            }

            
            if (venta.getProcesado() == 1) {
                redirectAttributes.addFlashAttribute("error", "Operación rechazada: Esta venta ya fue procesada e impactada en el stock.");
                return "redirect:/ventas/ver/" + id;
            }

            ventaService.procesar(id); 
            redirectAttributes.addFlashAttribute("success", "Venta procesada con éxito. Stock actualizado.");
            
        } catch (RuntimeException e) {
            
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas/ver/" + id;
    }

    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Venta venta = ventaService.buscarPorId(id);
        
        if (venta != null) {
            
            if (venta.getProcesado() == 1) {
                redirectAttributes.addFlashAttribute("error", "No es posible eliminar una venta que ya ha sido procesada.");
                return "redirect:/ventas/ver/" + id;
            }
            ventaService.eliminar(id);
        }
        return "redirect:/ventas";
    }

    @SuppressWarnings("unchecked")
    private List<DetalleVenta> obtenerDetalle(HttpSession session) {
        List<DetalleVenta> detalleVenta = (List<DetalleVenta>) session.getAttribute("detalleVenta");
        if (detalleVenta == null) {
            detalleVenta = new ArrayList<>();
            session.setAttribute("detalleVenta", detalleVenta);
        }
        return detalleVenta;
    }

    @GetMapping("/factura/{id}")
    public String verFactura(@PathVariable Long id, Model model) {
        Venta venta = ventaService.buscarPorId(id);
        
        // Validamos que la venta exista y esté procesada para poder facturarse
        if (venta == null || venta.getProcesado() == 0) {
            return "redirect:/ventas";
        }

        model.addAttribute("title", "Comprobante de Venta / Factura");
        model.addAttribute("venta", venta);
        model.addAttribute("detalles", venta.getDetalles());
        
        return "ventas/factura";
    }

    @Controller
    @RequestMapping("/reportes")
    public class ReporteController {

        private final VentaService ventaService;

        public ReporteController(VentaService ventaService) {
            this.ventaService = ventaService;
        }

        @GetMapping("/resumen-mensual")
        public String resumenMensual(
                @RequestParam(required = false) Integer anio,
                @RequestParam(required = false) Integer mes,
                Model model) {
            
            // Si no se envían parámetros, usamos la fecha actual del servidor (Año 2026)
            LocalDate fechaActual = LocalDate.now();
            int anioFiltro = (anio != null) ? anio : fechaActual.getYear();
            int mesFiltro = (mes != null) ? mes : fechaActual.getMonthValue();

            model.addAttribute("title", "Resumen Mensual Estadístico por Producto");
            model.addAttribute("anioFiltro", anioFiltro);
            model.addAttribute("mesFiltro", mesFiltro);
            
            // Pasamos la lista agrupada al modelo de Thymeleaf
            model.addAttribute("datosReporte", ventaService.obtenerReporteMensual(anioFiltro, mesFiltro));

            return "reportes/resumen-mensual";
        }
    }
}