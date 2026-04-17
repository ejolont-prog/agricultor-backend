package com.example.agricultor.repository;
import com.example.agricultor.model.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
    // Busca todos los elementos de un grupo específico (ej: todas las unidades de medida)
    List<Catalogo> findByIdcatalogo(Integer idcatalogo);
}