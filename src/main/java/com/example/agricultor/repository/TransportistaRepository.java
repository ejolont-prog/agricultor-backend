package com.example.agricultor.repository;

import com.example.agricultor.model.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
    boolean existsByCui(String cui);
}