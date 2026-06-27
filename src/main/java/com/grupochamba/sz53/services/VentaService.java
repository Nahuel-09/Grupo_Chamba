package com.grupochamba.sz53.services;

import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.repositories.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class VentaService {

    private final VentaRepositoryJPA ventaRepository;
    private final ProductoRepositoryJPA productoRepository;

    public VentaService(VentaRepositoryJPA ventaRepository,
                        ProductoRepositoryJPA productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Venta guardar(Venta venta) {
        if (venta.getCliente() == null) {
            throw new RuntimeException("La venta debe tener un cliente.");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un producto.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(venta);
            detalle.calcularImportes();
        }

        venta.calcularTotales();
        return ventaRepository.save(venta);
    }


    @Transactional
    public void procesar(Long ventaId) {
        Venta venta = buscarPorId(ventaId);

        if (venta == null) {
            throw new RuntimeException("La venta no existe.");
        }

        if (venta.estaProcesada() || venta.getProcesado() == 1) {
            throw new RuntimeException("La venta ya fue procesada.");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("No se puede procesar una venta sin detalles.");
        }

    
        validarYDescontarStock(venta);

        venta.calcularTotales();
        venta.setProcesado(1);

        ventaRepository.save(venta);
    }

    private void validarYDescontarStock(Venta venta) {
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a cero.");
            }

            Producto producto = detalle.getProducto();
            if (producto == null || producto.getId() == null) {
                throw new RuntimeException("El detalle contiene un producto inválido.");
            }

        
            Producto productoBD = productoRepository.findById(producto.getId())
                    .orElseThrow(() -> new RuntimeException("El producto '" + producto.getNombre() + "' ya no existe en el catálogo."));

            if (productoBD.getStock() == null) {
                throw new RuntimeException("El producto '" + productoBD.getNombre() + "' no tiene stock definido.");
            }

        
            if (productoBD.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + productoBD.getNombre() 
                        + " (Pedido: " + detalle.getCantidad() + " | Stock actual: " + productoBD.getStock() + ")");
            }

        
            productoBD.setStock(productoBD.getStock() - detalle.getCantidad());
            productoRepository.save(productoBD);
        }
    }

    @Transactional
    public void eliminar(Long id) {
        Venta venta = buscarPorId(id);

        if (venta == null) {
            return;
        }

        if (venta.estaProcesada() || venta.getProcesado() == 1) {
            throw new RuntimeException("No se puede eliminar una venta que ya ha sido procesada.");
        }

        ventaRepository.deleteById(id);
    }

    public List<VentaRepositoryJPA.ResumenMensualProducto> obtenerReporteMensual(int anio, int mes) {
        return ventaRepository.obtenerResumenMensual(anio, mes);
    }
}