package com.grupochamba.sz53.services;

import java.util.*;

import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.repositories.*;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotaEnvioService {

    private final NotaEnvioRepositoryJPA notaEnvioRepository;
    private final DetalleNotaEnvioRepositoryJPA detalleRepository;
    private final ProductoRepositoryJPA productoRepository; 

    public NotaEnvioService(
            NotaEnvioRepositoryJPA notaEnvioRepository,
            DetalleNotaEnvioRepositoryJPA detalleRepository,
            ProductoRepositoryJPA productoRepository) {
        this.notaEnvioRepository = notaEnvioRepository;
        this.detalleRepository = detalleRepository;
        this.productoRepository = productoRepository;
    }

    public List<NotaEnvio> listar() {
        return notaEnvioRepository.findAll();
    }

    public NotaEnvio buscarPorId(Long id) {
        return notaEnvioRepository.findById(id).orElse(null);
    }

    @Transactional
    public NotaEnvio guardar(NotaEnvio nota) {
        return notaEnvioRepository.save(nota);
    }

    public Boolean esNuevaEditable(NotaEnvio nota) {
        return nota != null
                && nota.getVerificado() != null
                && nota.getProcesado() != null
                && nota.getVerificado() == 0
                && nota.getProcesado() == 0;
    }

    public Boolean esVerificadaNoProcesada(NotaEnvio nota) {
        return nota != null
                && nota.getVerificado() != null
                && nota.getProcesado() != null
                && nota.getVerificado() == 1
                && nota.getProcesado() == 0;
    }

    public Boolean esProcesada(NotaEnvio nota) {
        return nota != null
                && nota.getVerificado() != null
                && nota.getProcesado() != null
                && nota.getVerificado() == 1
                && nota.getProcesado() == 1;
    }

    @Transactional
    public void actualizarCabecera(Long id, NotaEnvio datosFormulario) {
        NotaEnvio nota = buscarPorId(id);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esNuevaEditable(nota)) {
            throw new RuntimeException("No se puede modificar la cabecera: La nota no está en estado Nuevo/Editable.");
        }

        nota.setFecha(datosFormulario.getFecha());
        nota.setNro(datosFormulario.getNro());
        nota.setProveedor(datosFormulario.getProveedor());
        nota.setObservacion(datosFormulario.getObservacion());

        notaEnvioRepository.save(nota);
    }

    @Transactional
    public void agregarDetalleGuardado(Long notaId, DetalleNotaEnvio detalle) {
        NotaEnvio nota = buscarPorId(notaId);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esNuevaEditable(nota)) {
            throw new RuntimeException("Operación rechazada: La nota no se encuentra en estado Nuevo/Editable.");
        }

        if (detalle == null || detalle.getProducto() == null) {
            throw new RuntimeException("El detalle debe contener un producto válido.");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad ingresada debe ser mayor a cero.");
        }

        if (detalle.getPrecio() == null || detalle.getPrecio() <= 0) {
            throw new RuntimeException("El precio ingresado debe ser mayor a cero.");
        }

        detalle.setNotaEnvio(nota);
        nota.getDetalles().add(detalle);

        notaEnvioRepository.save(nota);
    }

    @Transactional
    public void eliminarDetalleGuardado(Long notaId, Long detalleId) {
        NotaEnvio nota = buscarPorId(notaId);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esNuevaEditable(nota)) {
            throw new RuntimeException("Operación rechazada: La nota está bloqueada.");
        }

        DetalleNotaEnvio detalle = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("El detalle de la nota no existe."));

        if (detalle.getNotaEnvio() == null || !detalle.getNotaEnvio().getId().equals(notaId)) {
            throw new RuntimeException("El detalle no pertenece a la nota de envío especificada.");
        }

        nota.getDetalles().remove(detalle);
        detalleRepository.delete(detalle);
    }

    @Transactional
    public void eliminarNota(Long id) {
        NotaEnvio nota = buscarPorId(id);
        if (nota == null) return;
        if (!esNuevaEditable(nota)) {
            throw new RuntimeException("No se puede eliminar una nota de envío verificada o procesada.");
        }
        notaEnvioRepository.delete(nota);
    }

    @Transactional
    public void verificar(Long id) {
        NotaEnvio nota = buscarPorId(id);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esNuevaEditable(nota)) {
            throw new RuntimeException("La nota ya ha sido verificada o procesada previamente.");
        }

        if (nota.getDetalles() == null || nota.getDetalles().isEmpty()) {
            throw new RuntimeException("No se puede verificar una nota de envío sin artículos en el detalle.");
        }

        nota.setVerificado(1);
        nota.setProcesado(0);

        notaEnvioRepository.save(nota);
    }

    @Transactional
    public void desverificar(Long id) {
        NotaEnvio nota = buscarPorId(id);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esVerificadaNoProcesada(nota)) {
            throw new RuntimeException("Únicamente se pueden desverificar notas que no hayan sido procesadas en stock.");
        }

        nota.setVerificado(0);
        nota.setProcesado(0);

        notaEnvioRepository.save(nota);
    }

    
    @Transactional
    public void procesar(Long id) {
        NotaEnvio nota = buscarPorId(id);
        if (nota == null) throw new RuntimeException("La nota de envío no existe.");
        if (!esVerificadaNoProcesada(nota)) {
            throw new RuntimeException("Operación inválida: La nota debe estar Verificada y No Procesada.");
        }

        if (nota.getDetalles() == null || nota.getDetalles().isEmpty()) {
            throw new RuntimeException("No se puede procesar una nota de envío sin detalles.");
        }

        for (DetalleNotaEnvio detalle : nota.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto == null) {
                throw new RuntimeException("Inconsistencia detectada: Un ítem del detalle no referencia a un producto.");
            }

            
            Producto productoBD = productoRepository.findById(producto.getId())
                    .orElseThrow(() -> new RuntimeException("El producto '" + producto.getNombre() + "' ya no existe en el catálogo."));

            int stockActual = (productoBD.getStock() != null) ? productoBD.getStock() : 0;
            int cantidadRecibida = (detalle.getCantidad() != null) ? detalle.getCantidad() : 0;

            
            productoBD.setStock(stockActual + cantidadRecibida);

            
            if (detalle.getPrecio() != null && detalle.getPrecio() > 0) {
                productoBD.setPrecio(detalle.getPrecio());
            }

            productoRepository.save(productoBD);
        }

        nota.setVerificado(1);
        nota.setProcesado(1); 

        notaEnvioRepository.save(nota);
    }
}