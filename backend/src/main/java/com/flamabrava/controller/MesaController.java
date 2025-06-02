package com.flamabrava.controller;

import com.flamabrava.model.Mesa;
import com.flamabrava.service.MesaService;
import com.flamabrava.exception.MesaNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Mesa savedMesa = mesaService.save(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMesa);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMesa(@PathVariable Integer id) {
        mesaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<? extends Object> changeMesaEstado(@PathVariable Integer id, @RequestBody String estado) {
        return mesaService.findById(id)
                .map(mesa -> {
                    if (estado == null || estado.isEmpty()) {
                        return ResponseEntity.badRequest().body(null);
                    }
                    mesa.setEstado(estado);
                    return ResponseEntity.ok(mesaService.save(mesa));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/numero-capacidad")
    public ResponseEntity<Mesa> updateNumeroYCapacidad(@PathVariable Integer id, @RequestBody Mesa mesaDetails) {
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

}
