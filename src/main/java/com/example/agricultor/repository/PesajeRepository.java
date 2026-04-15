package com.example.agricultor.repository;

import com.example.agricultor.model.Pesaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PesajeRepository extends JpaRepository<Pesaje, Long> {
    // Para cumplir con el flujo de mostrar solo registros activos [cite: 94]
    List<Pesaje> findByEliminadoFalse();
}