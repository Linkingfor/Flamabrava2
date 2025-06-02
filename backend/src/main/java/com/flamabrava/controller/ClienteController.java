package com.flamabrava.controller;

import com.flamabrava.model.Cliente;
import com.flamabrava.model.Usuario;
import com.flamabrava.service.ClienteService;
import com.flamabrava.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Integer id) {
        Optional<Cliente> cliente = clienteService.findById(id);

        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String contrasena = credentials.get("contrasena");

        Optional<Cliente> cliente = clienteService.findByEmail(email);
        if (cliente.isPresent()) {
            Cliente clienteLogueado = cliente.get();
            if (clienteLogueado.getContrasena().equals(contrasena)) {
                clienteLogueado.setIsActive(true);
                clienteService.save(clienteLogueado);
                return ResponseEntity.ok(clienteLogueado);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta para cliente");
            }
        }

        Optional<Usuario> usuario = usuarioService.findByNombre(email);
        if (usuario.isPresent()) {
            Usuario usuarioLogueado = usuario.get();
            if (usuarioLogueado.getPassword().equals(contrasena)) {
                return ResponseEntity.ok(usuarioLogueado);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta para usuario");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falta el correo electrónico");
        }

        Optional<Cliente> cliente = clienteService.findByEmail(email);

        if (cliente.isPresent()) {
            Cliente clienteLogueado = cliente.get();
            clienteLogueado.setIsActive(false);
            clienteService.save(clienteLogueado);
            return ResponseEntity.ok("Cierre de sesión exitoso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateTelefono(@PathVariable Integer id, @RequestBody Cliente cliente) {
        Optional<Cliente> updatedCliente = clienteService.updateTelefono(id, cliente.getTelefono());

        return updatedCliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping("")
    public ResponseEntity<Cliente> registerCliente(@RequestBody Cliente cliente) {
        Optional<Cliente> existingCliente = clienteService.findByEmail(cliente.getEmail());

        if (existingCliente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        if (cliente.getRol() == null || cliente.getRol().isEmpty()) {
            cliente.setRol("Cliente");
        }

        Cliente savedCliente = clienteService.save(cliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCliente(@PathVariable Integer id) {
        try {
            Optional<Cliente> cliente = clienteService.findById(id);
            if (cliente.isPresent()) {
                clienteService.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el cliente: " + e.getMessage());
        }
    }

}
