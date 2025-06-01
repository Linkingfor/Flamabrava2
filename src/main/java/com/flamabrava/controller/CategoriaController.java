package com.flamabrava.controller;

import com.flamabrava.model.Categoria;
import com.flamabrava.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaService.findAll();
        return new ResponseEntity<>(categorias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoriaById(@PathVariable Integer id) {
        Optional<Categoria> categoria = categoriaService.findById(id);
        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Categoria nuevaCategoria = categoriaService.save(categoria);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable Integer id,
            @RequestBody Categoria categoriaDetails) {
        Optional<Categoria> categoriaOptional = categoriaService.findById(id);

        if (categoriaOptional.isPresent()) {
            Categoria categoriaToUpdate = categoriaOptional.get();
            categoriaToUpdate.setNombre(categoriaDetails.getNombre());
            Categoria categoriaActualizada = categoriaService.save(categoriaToUpdate);
            return ResponseEntity.ok(categoriaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id) {
        if (!categoriaService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/categorias")
    public List<Categoria> getCategorias() {
        return categoriaService.findAll();
    }

}
