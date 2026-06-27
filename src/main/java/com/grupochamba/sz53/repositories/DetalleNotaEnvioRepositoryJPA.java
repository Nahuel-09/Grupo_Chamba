package com.grupochamba.sz53.repositories;

import com.grupochamba.sz53.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface DetalleNotaEnvioRepositoryJPA extends JpaRepository<DetalleNotaEnvio, Long> {

}