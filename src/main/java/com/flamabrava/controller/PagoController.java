package com.flamabrava.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.flamabrava.model.Cliente;
import com.flamabrava.model.DetallePedido;
import com.flamabrava.model.Pedido;
import com.flamabrava.model.Producto;
import com.flamabrava.service.PedidoService;
import com.flamabrava.service.ProductoService;
import com.flamabrava.service.StripeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = {
    "http://localhost:3000",
    "https://polleriaflamabrava.netlify.app"
})
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProductoService productoService;

    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> body) {
        logger.info(">>> Llega petición a /create-session con body: {}", body);

        try {
            // 1️⃣ Parámetros básicos
            String description = (String) body.get("description");
            String successUrl  = (String) body.get("successUrl");
            String cancelUrl   = (String) body.get("cancelUrl");
            Integer clienteId  = ((Number) body.get("clienteId")).intValue();

            // 2️⃣ Crear Pedido
            Pedido pedido = new Pedido();
            pedido.setCliente(new Cliente(clienteId));
            pedido.setEstado("Pendiente");
            pedido.setFechaPedido(new Date());

            // 3️⃣ Procesar líneas
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            if (items == null || items.isEmpty()) {
                return ResponseEntity.badRequest()
                                     .body(Map.of("error", "El carrito debe incluir al menos un ítem."));
            }
            List<DetallePedido> detalle = new ArrayList<>();
            for (Map<String, Object> it : items) {
                Integer prodId = ((Number) it.get("productoId")).intValue();
                Integer cantidad = ((Number) it.get("cantidad")).intValue();

                Producto producto = productoService.findById(prodId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + prodId));

                DetallePedido dp = new DetallePedido();
                dp.setPedido(pedido);
                dp.setProducto(producto);
                dp.setCantidad(cantidad);

                BigDecimal totalLinea = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
                dp.setTotal(totalLinea);

                detalle.add(dp);
            }
            pedido.setDetalle(detalle);

            // 4️⃣ Calcular totales y texto
BigDecimal sumaTotal = detalle.stream()
    .map(DetallePedido::getTotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
pedido.setTotal(sumaTotal);

// aquí añadimos el subtotal en cada ítem
String resumen = detalle.stream()
    .map(d -> d.getCantidad()
        + "× " + d.getProducto().getNombre()
        + " (S/ " + d.getTotal().setScale(2, RoundingMode.HALF_UP) + ")"
    )
    .collect(Collectors.joining(", "));
pedido.setDetalles(resumen);

// 5️⃣ Guardar pedido
Pedido saved = pedidoService.save(pedido);

            // 6️⃣ Crear sesión Stripe pasando _todos_ los argumentos, incluido detalle
            long amountForStripe = sumaTotal.multiply(BigDecimal.valueOf(100)).longValue();
            Session session = stripeService.createCheckoutSession(
                amountForStripe,                       // <-- precio en centavos
                description,
                successUrl + "?pedidoId=" + saved.getId(),
                cancelUrl,
                Map.of("pedido_id", saved.getId().toString()),
                detalle                                 // <-- ¡aquí va la lista de detalles!
            );

            logger.info("<<< Sesión creada OK: sessionId={}", session.getId());
            return ResponseEntity.ok(Map.of(
                "sessionId",      session.getId(),
                "publishableKey", stripeService.getPublishableKey(),
                "pedidoId",       saved.getId().toString()
            ));

        } catch (StripeException e) {
            logger.error("Error de Stripe al crear sesión:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Stripe error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error genérico en createSession:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}