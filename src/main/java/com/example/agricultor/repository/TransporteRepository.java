package com.example.agricultor.repository;

import com.example.agricultor.model.Transporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransporteRepository extends JpaRepository<Transporte, Long> {
    boolean existsByPlaca(String placa);
}