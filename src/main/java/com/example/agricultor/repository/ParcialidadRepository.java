package com.example.agricultor.repository;

import com.example.agricultor.dto.ParcialidadConPlacaProjection;
import com.example.agricultor.model.Parcialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParcialidadRepository extends JpaRepository<Parcialidad, Integer> {

    // Para listar parcialidades de un pesaje específico que no estén eliminadas
    //List<Parcialidad> findByIdpesajeAndEliminadoFalse(Integer idpesaje);

    @Query(value = """
    SELECT 
        p.idparcialidad,
        p.idpesaje,
        p.idtransporte,
        t.placa,
        p.idtransportista,
        ts.nombrecompleto,
        p.estadoparcialidad,
        p.pesoestimadoparcialidad,
        p.textorechazado,
        p.qr,
        p.faltante,
        p.sobrante,
        p.fechacreacion,
        p.creadopor,
        p.modificadopor,
        p.fechamodificacion,
        p.eliminado,
        p.tipomedida
    FROM agricultor.parcialidades p
    LEFT JOIN agricultor.transportes t ON t.idtransporte = p.idtransporte
    LEFT JOIN agricultor.transportistas ts ON ts.idtransportista = p.idtransportista
    WHERE p.idpesaje = :idpesaje AND p.eliminado = false
""", nativeQuery = true)
    List<ParcialidadConPlacaProjection> findByIdpesajeAndEliminadoFalse(@Param("idpesaje") Integer idpesaje);
}