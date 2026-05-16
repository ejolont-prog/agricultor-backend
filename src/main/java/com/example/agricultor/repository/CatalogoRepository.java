package com.example.agricultor.repository;

import com.example.agricultor.model.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {

    List<Catalogo> findByIdcatalogo(Long idcatalogo);

    List<Catalogo> findByIdcatalogoAndEstadoTrue(Long idcatalogo);

    @Query(value = "SELECT id FROM agricultor.catalogos WHERE TRIM(nombre) ILIKE TRIM(?1) AND idcatalogo = 4 LIMIT 1", nativeQuery = true)
    Integer findIdByNombreAndCatalogoCuatro(String nombre);

    Optional<Catalogo> findByNombre(String nombre);
}