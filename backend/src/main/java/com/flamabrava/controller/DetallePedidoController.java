package com.flamabrava.controller;

import com.flamabrava.model.DetallePedido;
import com.flamabrava.service.DetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoService detallePedidoService;

    @GetMapping
    public List<DetallePedido> getAllDetallesPedido() {
        return detallePedidoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> getDetallePedidoById(@PathVariable Integer id) {
        Optional<DetallePedido> detallePedido = detallePedidoService.findById(id);
        return detallePedido.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DetallePedido createDetallePedido(@RequestBody DetallePedido detallePedido) {
        return detallePedidoService.save(detallePedido);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedido> updateDetallePedido(@PathVariable Integer id,
            @RequestBody DetallePedido detallePedidoDetails) {
        Optional<DetallePedido> detallePedido = detallePedidoService.findById(id);
        if (detallePedido.isPresent()) {
            DetallePedido detallePedidoToUpdate = detallePedido.get();
            detallePedidoToUpdate.setPedido(detallePedidoDetails.getPedido());
            detallePedidoToUpdate.setProducto(detallePedidoDetails.getProducto());
            detallePedidoToUpdate.setCantidad(detallePedidoDetails.getCantidad());
            detallePedidoToUpdate.setTotal(detallePedidoDetails.getTotal());
            return ResponseEntity.ok(detallePedidoService.save(detallePedidoToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetallePedido(@PathVariable Integer id) {
        detallePedidoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
