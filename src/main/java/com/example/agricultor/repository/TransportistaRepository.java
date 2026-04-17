package com.example.agricultor.repository;

import com.example.agricultor.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {

    /**
     * Busca transportistas filtrados por el ID del usuario creador
     * y que no estén marcados como eliminados.
     */
    List<Transportista> findByCreadoporAndEliminadoFalse(Integer creadopor);
}