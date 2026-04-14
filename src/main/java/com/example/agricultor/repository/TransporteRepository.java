package com.example.agricultor.repository;

import com.example.agricultor.model.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Long> {
    // Busca transportes que no estén marcados como eliminados
    List<Transporte> findByEliminadoFalse();
}