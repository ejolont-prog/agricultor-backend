package com.example.agricultor.repository;

import com.example.agricultor.model.Pesaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PesajeRepository extends JpaRepository<Pesaje, Long> {
    // Para cumplir con el flujo de mostrar solo registros activos [cite: 94]

    // El nombre debe ser findBy + NombreDelCampo + And + Condición
    List<Pesaje> findByIdperfilagricultorAndEliminadoFalse(Long idperfilagricultor);


    @Modifying(clearAutomatically = true, flushAutomatically = true) // Agrega esto
    @Query("UPDATE Pesaje p SET p.cantidadparcialidades = COALESCE(p.cantidadparcialidades, 0) + 1 WHERE p.idpesaje = :id")
    void incrementarContadorParcialidades(@Param("id") Integer id);

    Optional<Pesaje> findByNocuenta(String nocuenta);
}