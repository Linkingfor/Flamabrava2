package com.flamabrava.controller;

import com.flamabrava.model.Usuario;
import com.flamabrava.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        String nombre = loginRequest.getNombre();
        String contrasena = loginRequest.getPassword();

        Optional<Usuario> usuarioOpt = usuarioService.findByNombre(nombre);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getPassword().equals(contrasena)) {
                if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                    usuario.setRol("Cliente");
                }
                return ResponseEntity.ok(usuario);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos");
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {

        if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
            usuario.setRol("Trabajador");
        }

        Usuario newUser = usuarioService.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            Usuario usuarioToUpdate = usuario.get();

            if (usuarioDetails.getNombre() != null && !usuarioDetails.getNombre().isEmpty()) {
                usuarioToUpdate.setNombre(usuarioDetails.getNombre());
            }
            if (usuarioDetails.getPassword() != null && !usuarioDetails.getPassword().isEmpty()) {
                usuarioToUpdate.setPassword(usuarioDetails.getPassword());
            }
            if (usuarioDetails.getRol() != null && !usuarioDetails.getRol().isEmpty()) {
                usuarioToUpdate.setRol(usuarioDetails.getRol());
            }

            Usuario updatedUser = usuarioService.save(usuarioToUpdate);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
