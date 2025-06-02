package com.flamabrava.service;

import com.flamabrava.model.Reserva;
import com.flamabrava.repository.ReservaRepository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Service
public class ReservaService {

    private static final int AFORO_MAXIMO = 30;

    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Calcula cuántas plazas (numPersonas) quedan disponibles para la "hora en punto"
     * dada. Por ejemplo, si recibe 2025-06-02T16:37:45, lo trunca a 2025-06-02T16:00:00
     * y consulta cuántas personas hay entre 16:00 (inclusive) y 17:00 (exclusive).
     *
     * @param fechaHoraOriginal la fecha/hora exacta de la reserva (LocalDateTime)
     * @return número de plazas restantes (entre 0 y AFORO_MAXIMO)
     */
    public int obtenerAforoRestante(LocalDateTime fechaHoraOriginal) {
        // 1) Truncar a "hora en punto" (minutos=0, segundos=0, nanos=0)
        LocalDateTime horaInicio = fechaHoraOriginal.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime horaFin    = horaInicio.plusHours(1);

        // 2) Llamar al repositorio para sumar numPersonas en ese intervalo
        Integer totalReservadas = reservaRepository.sumPersonasEnRango(horaInicio, horaFin);
        if (totalReservadas == null) {
            totalReservadas = 0;
        }

        // 3) Calcular plazas restantes (no negativas)
        int restante = AFORO_MAXIMO - totalReservadas;
        return Math.max(restante, 0);
    }
}