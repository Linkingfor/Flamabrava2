package com.flamabrava.controller;

import com.flamabrava.model.Mesa;
import com.flamabrava.model.Reserva;
import com.flamabrava.service.MesaService;
import com.flamabrava.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public List<Reserva> getAllReservas() {
        return reservaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> getReservaById(@PathVariable Integer id) {
        Optional<Reserva> reserva = reservaService.findById(id);
        return reserva.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Reserva createReserva(@RequestBody Reserva reserva) {
        if (reserva.getClienteId() == null || reserva.getMesa() == null || reserva.getFecha() == null) {
            throw new IllegalArgumentException("Mesa, Cliente y Fecha son obligatorios");
        }

        Integer mesaId = Integer.valueOf(reserva.getMesa().getId());

        Optional<Mesa> mesa = mesaService.findById(mesaId);
        if (!mesa.isPresent()) {
            throw new IllegalArgumentException("La mesa seleccionada no existe");
        }

        if (!mesa.get().getEstado().equals("Libre")) {
            throw new IllegalArgumentException("La mesa seleccionada no está disponible para la reserva");
        }

        if (reserva.getObservaciones() == null) {
            reserva.setObservaciones("");
        }

        Reserva reservaCreada = reservaService.save(reserva);

        mesa.get().setEstado("Ocupado");
        mesaService.save(mesa.get());

        return reservaCreada;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Integer id, @RequestBody Reserva reservaDetails) {
        Optional<Reserva> reserva = reservaService.findById(id);
        if (reserva.isPresent()) {
            Reserva reservaToUpdate = reserva.get();
            reservaToUpdate.setMesa(reservaDetails.getMesa());
            reservaToUpdate.setFecha(reservaDetails.getFecha());
            reservaToUpdate.setObservaciones(reservaDetails.getObservaciones());
            reservaToUpdate.setClienteId(reservaDetails.getClienteId());
            return ResponseEntity.ok(reservaService.save(reservaToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Integer id) {
        reservaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
