package com.flamabrava.controller;

import com.flamabrava.exception.MesaNotFoundException;
import com.flamabrava.model.Mesa;
import com.flamabrava.repository.MesaRepository;
import com.flamabrava.repository.ReservaRepository;
import com.flamabrava.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @Autowired
    private MesaRepository mesaRepository;

    // Si necesitas contar o filtrar reservas directamente:
    @Autowired
    private ReservaRepository reservaRepository;

    private void validateMesa(Mesa mesa) {
        if (mesa.getNumero() <= 0 || mesa.getCapacidad() <= 0) {
            throw new IllegalArgumentException("Número y capacidad deben ser positivos.");
        }
    }

    @GetMapping
    public List<Mesa> getAllMesas() {
        return mesaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mesa> getMesaById(@PathVariable Integer id) {
        return mesaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<Mesa> createMesa(@RequestBody Mesa mesa) {
        validateMesa(mesa);
        Mesa saved = mesaService.save(mesa);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMesa(
            @PathVariable Integer id,
            @RequestBody Mesa detalles
    ) {
        return mesaService.findById(id)
                .map(mesa -> {
                    if (detalles.getEstado() != null && !detalles.getEstado().equals(mesa.getEstado())) {
                        mesa.setEstado(detalles.getEstado());
                        mesaService.save(mesa);
                        return ResponseEntity.ok(mesa);
                    }
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Integer id) {
        mesaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> changeEstado(
            @PathVariable Integer id,
            @RequestBody String estado
    ) {
        return mesaService.findById(id)
                .map(mesa -> {
                    if (estado == null || estado.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body("Estado inválido");
                    }
                    mesa.setEstado(estado.trim());
                    mesaService.save(mesa);
                    return ResponseEntity.ok(mesa);
                })
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    @PutMapping("/{id}/numero-capacidad")
    public ResponseEntity<Mesa> updateNumeroYCapacidad(
            @PathVariable Integer id,
            @RequestBody Mesa detalles
    ) {
        return mesaService.findById(id)
                .map(mesa -> {
                    if (detalles.getNumero() <= 0 || detalles.getCapacidad() <= 0) {
                        throw new IllegalArgumentException("Número y capacidad deben ser positivos.");
                    }
                    mesa.setNumero(detalles.getNumero());
                    mesa.setCapacidad(detalles.getCapacidad());
                    return ResponseEntity.ok(mesaService.save(mesa));
                })
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    // ──────────────────────────────────────────────────────────────────────
    // Nuevo: mesas disponibles en la hora “en punto” exacta
    // ──────────────────────────────────────────────────────────────────────

    /**
     * GET /api/mesas/disponibles
     *
     * @param fecha        ISO date-time, p.ej. 2025-06-02T16:00:00
     * @param numPersonas  capacidad exacta requerida
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Mesa>> getMesasDisponibles(
        @RequestParam("fecha")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime fecha,

        @RequestParam("numPersonas")
        Integer numPersonas
    ) {
        // 1) Truncar a “hora en punto”
        LocalDateTime horaInicio = fecha.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime horaFin    = horaInicio.plusHours(1);

        // 2) Query JPA custom: capacidad exacta + no reservadas en ese slot
        List<Mesa> disponibles = mesaRepository
            .findMesasDisponiblesExactas(horaInicio, horaFin, numPersonas);

        return ResponseEntity.ok(disponibles);
    }
}
