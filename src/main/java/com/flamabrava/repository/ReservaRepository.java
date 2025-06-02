package com.flamabrava.repository;

import com.flamabrava.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.time.LocalDateTime;

@Repository
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    /**
     * Suma cuántas personas (numPersonas) hay reservadas en un rango de una hora.
     * 
     * @param horaInicio LocalDateTime con minutos=0 y segundos=0 (p.ej. 2025-06-02T16:00:00)
     * @param horaFin    LocalDateTime una hora después (p.ej. 2025-06-02T17:00:00)
     * @return           Suma total de numPersonas en ese intervalo; si no hay nada, devuelve 0.
     */
    @Query("SELECT COALESCE(SUM(r.numPersonas), 0) " +
           "FROM Reserva r " +
           "WHERE r.fecha >= :horaInicio " +
           "  AND r.fecha < :horaFin " +
           "  AND r.estado <> 'cancelada'")
    Integer sumPersonasEnRango(
        @Param("horaInicio") LocalDateTime horaInicio,
        @Param("horaFin")    LocalDateTime horaFin
    );
}