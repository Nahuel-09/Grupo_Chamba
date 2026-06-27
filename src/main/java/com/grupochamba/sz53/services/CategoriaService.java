package com.grupochamba.sz53.services;

import com.grupochamba.sz53.repositories.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.grupochamba.sz53.model.*;

@Service
public class CategoriaService {
    private final CategoriaRepositoryJPA catRepo;

    public CategoriaService(CategoriaRepositoryJPA catRepo) {
        this.catRepo = catRepo;
    }

    public List<Categoria> listar() {
        return catRepo.findAllByOrderByNombreAsc();
    }

    public Categoria buscarpoid(int id) {
        return catRepo.findById(id).get();
    }

    public void crear(Categoria cat) {
        this.validarCategoria(cat);
        cat.setNombre(cat.getNombre().trim());
        catRepo.save(cat);
    }

    public void actualizar(Categoria cat) {
        this.validarCategoria(cat);
        cat.setNombre(cat.getNombre().trim());
        catRepo.save(cat);
    }

    public void eliminar(int id) {
        catRepo.deleteById(id);

    }

    public void validarCategoria(Categoria cat) {
        if (cat == null || cat.getNombre() == null || cat.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        Categoria categoria = catRepo.findByNombre(cat.getNombre());
        if ((categoria != null) && (cat.getId() != (categoria.getId()))) {
            throw new IllegalArgumentException("El nombre de la categoría ya existe");
        }

    }
}