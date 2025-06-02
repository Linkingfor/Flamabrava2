package com.flamabrava.controller;

import com.flamabrava.model.Inventario;
import com.flamabrava.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/inventarios")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public List<Inventario> getAllInventarios() {
        return inventarioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getInventarioById(@PathVariable Integer id) {
        Optional<Inventario> inventario = inventarioService.findById(id);
        return inventario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Inventario createInventario(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> updateInventario(@PathVariable Integer id,
            @RequestBody Inventario inventarioDetails) {
        Optional<Inventario> inventario = inventarioService.findById(id);
        if (inventario.isPresent()) {
            Inventario inventarioToUpdate = inventario.get();
            inventarioToUpdate.setFechaMovimiento(inventarioDetails.getFechaMovimiento());
            inventarioToUpdate.setProducto(inventarioDetails.getProducto());
            return ResponseEntity.ok(inventarioService.save(inventarioToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventario(@PathVariable Integer id) {
        inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
