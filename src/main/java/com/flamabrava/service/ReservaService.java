package com.flamabrava.service;

import com.flamabrava.model.Reserva;
import com.flamabrava.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Service
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> findById(Integer id) {
        return reservaRepository.findById(id);
    }

    public Reserva save(Reserva reserva) {

        if (reserva.getClienteId() == null || reserva.getMesa() == null || reserva.getFecha() == null) {
            throw new IllegalArgumentException("Cliente, Mesa y Fecha son obligatorios");
        }

        return reservaRepository.save(reserva);
    }

    public void deleteById(Integer id) {
        reservaRepository.deleteById(id);
    }
}
