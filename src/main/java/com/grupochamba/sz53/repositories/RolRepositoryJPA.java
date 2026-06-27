package com.grupochamba.sz53.repositories;

import java.util.*;
import com.grupochamba.sz53.model.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface RolRepositoryJPA extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}