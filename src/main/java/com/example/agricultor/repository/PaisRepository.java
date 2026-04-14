package com.example.agricultor.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import com.example.agricultor.model.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long>{

}
