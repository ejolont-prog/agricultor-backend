package com.example.agricultor.repository;

import com.example.agricultor.model.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

    // Busca todos los registros que pertenezcan al grupo (ej. 9 para licencias)
    // y que estén activos (estado = true)
    List<Catalogo> findByIdCatalogoAndEstadoTrue(Long idCatalogo);
}