package com.flamabrava.controller;

import com.flamabrava.model.Mesa;
import com.flamabrava.model.Reserva;
import com.flamabrava.repository.ReservaRepository;
import com.flamabrava.service.MesaService;
import com.flamabrava.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestionar reservas.
 */
@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private MesaService mesaService;

    /**
     * GET /api/reservas
     * Devuelve todas las reservas registradas.
     */
    @GetMapping
    public ResponseEntity<Iterable<Reserva>> getAllReservas() {
        return ResponseEntity.ok(reservaRepository.findAll());
    }

    /**
     * GET /api/reservas/{id}
     * Devuelve una reserva por su ID, o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Integer id) {
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        return reservaOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * POST /api/reservas
     *
     * 1) Valida que idUsuario, mesa, fecha y numPersonas estén presentes.
     * 2) Verifica que la mesa exista y esté libre.
     * 3) Guarda la reserva en la base de datos (con estado "Pendiente").
     * 4) Marca la mesa como “Ocupado”.
     * 5) Redondea fecha a “horaEnPunto” (minuto=0, segundo=0) y pide al servicio el aforo restante.
     * 6) Devuelve un JSON con la reserva creada + aforoRestante.
     *
     * Ejemplo de respuesta:
     * {
     *   "reserva": { ...todos los campos de la reserva... },
     *   "aforoRestante": 18
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReserva(@RequestBody Reserva reserva) {
        // 1) Validaciones mínimas
        if (reserva.getIdUsuario() == null ||
            reserva.getMesa() == null ||
            reserva.getFecha() == null ||
            reserva.getNumPersonas() == null) {
            throw new IllegalArgumentException("IdUsuario, Mesa, Fecha y numPersonas son obligatorios");
        }

        // 2) Verificamos que la mesa exista en la BD
        Integer mesaId = reserva.getMesa().getId();
        Optional<Mesa> mesaOpt = mesaService.findById(mesaId);
        if (!mesaOpt.isPresent()) {
            throw new IllegalArgumentException("La mesa seleccionada no existe");
        }
        Mesa mesaEncontrada = mesaOpt.get();

        // 3) Verificamos que la mesa esté libre ("Libre")
        if (!"Libre".equalsIgnoreCase(mesaEncontrada.getEstado())) {
            throw new IllegalArgumentException("La mesa seleccionada no está disponible para la reserva");
        }

        // 4) Asignamos estado inicial si viene nulo
        if (reserva.getEstado() == null) {
            reserva.setEstado("Pendiente");
        }

        // 5) Si no hay observaciones, dejamos cadena vacía
        if (reserva.getObservaciones() == null) {
            reserva.setObservaciones("");
        }

        // 6) Guardamos la reserva en BD
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 7) Marcamos la mesa como "Ocupado" y la guardamos
        mesaEncontrada.setEstado("Ocupado");
        mesaService.save(mesaEncontrada);

        // 8) Redondeamos la fecha a “hora en punto” (minuto=0, segundo=0)
        LocalDateTime horaOriginal = reservaGuardada.getFecha();
        LocalDateTime horaEnPunto = horaOriginal
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // 9) Obtenemos el aforo restante para esa horaEnPunto
        int aforoRestante = reservaService.obtenerAforoRestante(horaEnPunto);

        // 10) Preparamos la respuesta
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("reserva", reservaGuardada);
        respuesta.put("aforoRestante", aforoRestante);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * PUT /api/reservas/{id}
     *
     * Actualiza una reserva existente (no ajusta estado de mesa ni hace
     * validación de aforo aquí). Devuelve 404 si no existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(
            @PathVariable Integer id,
            @RequestBody Reserva reservaDetails) {

        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        if (!reservaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Reserva reservaToUpdate = reservaOpt.get();
        // Actualizamos únicamente los campos permitidos:
        reservaToUpdate.setIdUsuario(reservaDetails.getIdUsuario());
        reservaToUpdate.setMesa(reservaDetails.getMesa());
        reservaToUpdate.setFecha(reservaDetails.getFecha());
        reservaToUpdate.setNumPersonas(reservaDetails.getNumPersonas());
        reservaToUpdate.setObservaciones(reservaDetails.getObservaciones());
        // Si quieres permitir cambiar estado, agrégalo aquí:
        // reservaToUpdate.setEstado(reservaDetails.getEstado());

        Reserva reservaActualizada = reservaRepository.save(reservaToUpdate);
        return ResponseEntity.ok(reservaActualizada);
    }

    /**
     * DELETE /api/reservas/{id}
     *
     * Elimina la reserva con el ID dado. Antes, libera la mesa asociada
     * (cambiando su estado a "Libre"), luego borra la reserva. Devuelve 404 si no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Integer id) {
        if (!reservaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Liberamos la mesa antes de eliminar la reserva
        Optional<Reserva> porBorrar = reservaRepository.findById(id);
        if (porBorrar.isPresent()) {
            Mesa mesaDeReserva = porBorrar.get().getMesa();
            if (mesaDeReserva != null) {
                mesaDeReserva.setEstado("Libre");
                mesaService.save(mesaDeReserva);
            }
        }

        // Finalmente, eliminamos la reserva
        reservaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
