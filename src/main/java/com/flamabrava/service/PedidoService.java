package com.flamabrava.service;

import com.flamabrava.model.Pedido;
import com.flamabrava.model.Cliente;
import com.flamabrava.model.Producto;
import com.flamabrava.repository.PedidoRepository;
import com.flamabrava.repository.ClienteRepository;
import com.flamabrava.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Service
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Integer id) {
        return pedidoRepository.findById(id);
    }

    public Pedido save(Pedido pedido) {

        if (pedido.getCliente() != null && pedido.getCliente().getId() == null) {
            Cliente clientePersistido = clienteRepository.save(pedido.getCliente());
            pedido.setCliente(clientePersistido);
        }

        if (pedido.getProductos() != null && !pedido.getProductos().isEmpty()) {

            for (Producto producto : pedido.getProductos()) {
                Optional<Producto> productoExistente = productoRepository.findById(producto.getId());
                if (productoExistente.isPresent()) {
                    producto = productoExistente.get();
                } else {

                    throw new RuntimeException("Producto no encontrado");
                }
            }
        }

        return pedidoRepository.save(pedido);
    }

    public void deleteById(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
