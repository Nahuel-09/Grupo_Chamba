package com.grupochamba.sz53.repositories;

import java.util.*;
import com.grupochamba.sz53.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface CategoriaRepositoryJPA extends JpaRepository<Categoria, Integer> {
    Categoria findByNombre(String nombre);
    List<Categoria> findAllByOrderByIdAsc();
    List<Categoria> findAllByOrderByNombreAsc();
    
}