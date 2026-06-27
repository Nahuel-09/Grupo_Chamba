package com.grupochamba.sz53.services;

import java.util.*;
import com.grupochamba.sz53.model.*;
import com.grupochamba.sz53.repositories.*;
import org.springframework.stereotype.*;

@Service
public class ProductoService {

    private final ProductoRepositoryJPA repositorio;

    public ProductoService(ProductoRepositoryJPA repositorio) {
        this.repositorio = repositorio;
    }

    public List<Producto> lista() {
        return repositorio.findAll();
    }

    public List<Producto> listar() {
        return repositorio.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repositorio.findById(id).get();
    }

    public void guardar(Producto dato) {
        validar(dato);
        if (dato.getEstado() == null) {
            dato.setEstado(true);
        }
        repositorio.save(dato);
    }

    public void eliminar(Long id) {
        repositorio.deleteById(id);

    }

    public void validar(Producto dato) {

        if (dato.getNombre() == null || dato.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la Producto no puede estar vacío");
        }
        Producto existente = repositorio.findByNombre(dato.getNombre());
        if ((existente != null) && (dato.getId() != existente.getId())) {
            throw new IllegalArgumentException("El nombre de la Producto no puede ser repetido");
        }

        if ((dato.getCategoria() == null))
        {
            throw new IllegalArgumentException("El Categoria no puede estar vacío");
        }
        if (dato.getPrecio() == null || dato.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio del producto no puede ser nulo o negativo");
        }

    }

}