package com.flamabrava.service;

import com.flamabrava.model.Cliente;
import com.flamabrava.model.DetallePedido;
import com.flamabrava.model.Pedido;
import com.flamabrava.model.Producto;
import com.flamabrava.repository.ClienteRepository;
import com.flamabrava.repository.DetallePedidoRepository;
import com.flamabrava.repository.PedidoRepository;
import com.flamabrava.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    /** 1️⃣ Devuelve todos los pedidos con su detalle embebido */
    public List<Pedido> findAll() {
        return pedidoRepository.findAllWithDetalle();
    }

    /** 2️⃣ Devuelve un pedido (y detalle) por su ID */
    public Optional<Pedido> findByIdWithDetalle(Integer id) {
        return pedidoRepository.findByIdWithDetalle(id);
    }

    /**
     * 3️⃣ Guarda un pedido junto con sus líneas de detalle.
     */
    @Transactional
    public Pedido save(Pedido pedido) {
        // — 1️⃣ Cliente —
        Cliente cli = pedido.getCliente();
        if (cli != null && cli.getId() == null) {
            cli = clienteRepository.save(cli);
        }
        pedido.setCliente(cli);

        // — 2️⃣ Guarda la cabecera para generar ID —
        Pedido savedPedido = pedidoRepository.save(pedido);

        // — 3️⃣ Detalle de líneas —
        BigDecimal totalCalc = BigDecimal.ZERO;

        if (pedido.getDetalle() != null) {
            for (DetallePedido linea : pedido.getDetalle()) {
                // a) validar y recuperar producto persistido
                Producto prod = productoRepository.findById(linea.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado: " + linea.getProducto().getId()
                    ));

                // b) enlazar producto + pedido
                linea.setProducto(prod);
                linea.setPedido(savedPedido);

                // c) calcular subtotal = precio × cantidad
                //    aquí prod.getPrecio() ya es BigDecimal
                BigDecimal precioBD  = prod.getPrecio();
                BigDecimal cantidadBD = BigDecimal.valueOf(linea.getCantidad());
                BigDecimal subtotal  = precioBD.multiply(cantidadBD);

                linea.setTotal(subtotal);

                // d) persistir línea
                detallePedidoRepository.save(linea);

                // e) acumular total
                totalCalc = totalCalc.add(subtotal);
            }
        }

        // — 4️⃣ Actualiza total en cabecera y guarda —
        savedPedido.setTotal(totalCalc);
        return pedidoRepository.save(savedPedido);
    }

    /** 4️⃣ Elimina un pedido (y sus líneas en cascada) */
    public void deleteById(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
