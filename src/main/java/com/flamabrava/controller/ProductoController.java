package com.flamabrava.controller;

import com.flamabrava.model.Producto;
import com.flamabrava.model.Categoria;
import com.flamabrava.service.ProductoService;
import com.flamabrava.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.findById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {

        Optional<Categoria> categoria = categoriaService.findById(producto.getCategoria().getId());

        if (categoria.isPresent()) {

            producto.setCategoria(categoria.get());
            Producto nuevoProducto = productoService.save(producto);
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Integer id, @RequestBody Producto productoDetails) {
        Optional<Producto> producto = productoService.findById(id);
        if (producto.isPresent()) {
            Producto productoToUpdate = producto.get();
            productoToUpdate.setNombre(productoDetails.getNombre());
            productoToUpdate.setPrecio(productoDetails.getPrecio());
            productoToUpdate.setStock(productoDetails.getStock());

            Optional<Categoria> categoria = categoriaService.findById(productoDetails.getCategoria().getId());
            if (categoria.isPresent()) {
                productoToUpdate.setCategoria(categoria.get());
                Producto productoActualizado = productoService.save(productoToUpdate);
                return ResponseEntity.ok(productoActualizado);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.findById(id);
        if (producto.isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.ok("Producto eliminado con éxito.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado.");
        }
    }

    @GetMapping("/api/productos")
    public List<Producto> getProductos() {
        return productoService.findAll();
    }

}
