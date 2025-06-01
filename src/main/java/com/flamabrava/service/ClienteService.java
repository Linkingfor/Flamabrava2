package com.flamabrava.service;

import com.flamabrava.model.Cliente;
import com.flamabrava.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Service
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Integer id) {
        return clienteRepository.findById(id);
    }

    public void deleteById(Integer id) {
        clienteRepository.deleteById(id);
    }

    public Cliente save(Cliente cliente) {
        if (cliente.getRol() == null || cliente.getRol().isEmpty()) {
            cliente.setRol("Cliente");
        }
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Optional<Cliente> updateTelefono(Integer id, String telefono) {
        Optional<Cliente> cliente = clienteRepository.findById(id);

        if (cliente.isPresent()) {
            Cliente existingCliente = cliente.get();
            if (telefono != null && !telefono.isEmpty()) {
                existingCliente.setTelefono(telefono);
                clienteRepository.save(existingCliente);
                return Optional.of(existingCliente);
            }
        }
        return Optional.empty();
    }
}
