package com.example.agricultor.repository;

import com.example.agricultor.model.Transporte;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Long> {
    boolean existsByPlaca(String placa);

    @Modifying
    @Transactional
    @Query(value = "UPDATE agricultor.transportes SET disponible = false WHERE idtransporte = :id", nativeQuery = true)
    void marcarComoNoDisponible(@Param("id") Long id);
}

