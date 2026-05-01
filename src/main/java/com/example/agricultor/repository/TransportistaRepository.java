package com.example.agricultor.repository;

import com.example.agricultor.model.Transportista;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    boolean existsByCui(String cui);

    @Modifying
    @Transactional
    @Query(value = "UPDATE agricultor.transportistas SET disponible = false WHERE idtransportista = :id", nativeQuery = true)
    void marcarComoNoDisponible(@Param("id") Long id);
}