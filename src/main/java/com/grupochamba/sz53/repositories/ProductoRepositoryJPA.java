package com.grupochamba.sz53.repositories;

import java.util.List;
import com.grupochamba.sz53.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface ProductoRepositoryJPA extends JpaRepository<Producto, Long> {
    Producto findByNombre(String nombre);
    List<Producto> findAllByOrderByIdAsc();
    List<Producto> findAllByOrderByIdDesc();
    List<Producto> findAllByOrderByNombreAsc();
    List<Producto> findAllByOrderByNombreDesc();
    List<Producto> findByCategoriaOrderByNombreAsc(Categoria categoria);
    
    
}