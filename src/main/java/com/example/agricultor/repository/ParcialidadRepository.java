package com.example.agricultor.repository;

import com.example.agricultor.model.Parcialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParcialidadRepository extends JpaRepository<Parcialidad, Integer> {

    // Para listar parcialidades de un pesaje específico que no estén eliminadas
    List<Parcialidad> findByIdpesajeAndEliminadoFalse(Integer idpesaje);
}