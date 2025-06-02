package com.flamabrava.controller;

import com.flamabrava.model.Mesa;
import com.flamabrava.service.MesaService;
import com.flamabrava.exception.MesaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    private void validateMesa(Mesa mesa) {
        if (mesa.getNumero() <= 0 || mesa.getCapacidad() <= 0) {
            throw new IllegalArgumentException("Número y capacidad deben ser positivos.");
        }
    }

    /**
     * GET /api/mesas
     * Devuelve todas las mesas.
     */
    @GetMapping
    public List<Mesa> getAllMesas() {
        return mesaService.findAll();
    }

    /**
     * GET /api/mesas/{id}
     * Devuelve una sola mesa por ID, o lanza MesaNotFoundException si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mesa> getMesaById(@PathVariable Integer id) {
        return mesaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    /**
     * POST /api/mesas
     * Crea una nueva mesa (validando que número y capacidad sean positivos).
     */
    @PostMapping
    public ResponseEntity<Mesa> createMesa(@RequestBody Mesa mesa) {
        validateMesa(mesa);
        Mesa savedMesa = mesaService.save(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMesa);
    }

    /**
     * PUT /api/mesas/{id}
     * Actualiza únicamente el estado de la mesa (por ejemplo “Libre” u “Ocupado”).
     */
    @PutMapping("/{id}")
    public ResponseEntity<? extends Object> updateMesa(@PathVariable Integer id, @RequestBody Mesa mesaDetails) {
        return mesaService.findById(id)
                .map(mesa -> {
                    boolean updated = false;

                    if (mesaDetails.getEstado() != null && !mesaDetails.getEstado().equals(mesa.getEstado())) {
                        mesa.setEstado(mesaDetails.getEstado());
                        updated = true;
                    }

                    if (updated) {
                        return ResponseEntity.ok(mesaService.save(mesa));
                    } else {
                        return ResponseEntity.noContent().build();
                    }
                })
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    /**
     * DELETE /api/mesas/{id}
     * Elimina la mesa con el ID dado. 
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Integer id) {
        mesaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/mesas/{id}/estado
     * Cambia sólo el campo “estado” de la mesa (por ejemplo, "Libre" u "Ocupado").
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<? extends Object> changeMesaEstado(
            @PathVariable Integer id,
            @RequestBody String estado) {

        return mesaService.findById(id)
                .map(mesa -> {
                    if (estado == null || estado.trim().isEmpty()) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    mesa.setEstado(estado.trim());
                    return ResponseEntity.ok(mesaService.save(mesa));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/mesas/{id}/numero-capacidad
     * Actualiza número y capacidad de la mesa; ambos deben ser positivos.
     */
    @PutMapping("/{id}/numero-capacidad")
    public ResponseEntity<Mesa> updateNumeroYCapacidad(
            @PathVariable Integer id,
            @RequestBody Mesa mesaDetails) {

        return mesaService.findById(id)
                .map(mesa -> {
                    if (mesaDetails.getNumero() <= 0 || mesaDetails.getCapacidad() <= 0) {
                        throw new IllegalArgumentException("Número y capacidad deben ser positivos.");
                    }

                    mesa.setNumero(mesaDetails.getNumero());
                    mesa.setCapacidad(mesaDetails.getCapacidad());
                    return ResponseEntity.ok(mesaService.save(mesa));
                })
                .orElseThrow(() -> new MesaNotFoundException(id));
    }

    // ──────────────────────────────────────────────────────────────────────
    // Nuevo método: traer sólo las mesas disponibles en un rango de una hora
    // ──────────────────────────────────────────────────────────────────────

    /**
     * GET /api/mesas/disponibles
     *
     * Parámetros:
     *   - fecha (ISO date-time) p.ej. 2025-06-02T16:00:00
     *   - numPersonas (Integer)
     *
     * Devuelve todas las mesas que tengan capacidad >= numPersonas
     * y que no estén reservadas (estado distinto de 'cancelada')
     * en el intervalo [horaInicio, horaFin), donde:
     *   horaInicio = fecha pasada truncada a minutos=0 y segundos=0,
     *   horaFin    = horaInicio + 1 hora.
     *
     * Ejemplo de llamada:
     *   GET /api/mesas/disponibles?fecha=2025-06-02T16:00:00&numPersonas=10
     */
  @GetMapping("/disponibles")
    public ResponseEntity<List<Mesa>> getMesasDisponibles(
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam("numPersonas") Integer numPersonas
    ) {
        // Truncar a la hora "en punto"
        LocalDateTime horaInicio = fecha.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime horaFin    = horaInicio.plusHours(1);

        // Llamamos al servicio, que usará capacidad == numPersonas
        List<Mesa> disponibles = mesaService.findMesasDisponiblesExactas(
                horaInicio, horaFin, numPersonas
        );
        return ResponseEntity.ok(disponibles);
    }
}
