package com.flamabrava.repository;

import com.flamabrava.model.Cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    java.util.Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findById(Integer id);
}
