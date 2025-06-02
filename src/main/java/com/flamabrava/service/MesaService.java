package com.flamabrava.service;

import com.flamabrava.model.Mesa;
import com.flamabrava.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> findAll() {
        return mesaRepository.findAll();
    }

    public Optional<Mesa> findById(Integer id) {
        return mesaRepository.findById(id);
    }

    public Mesa save(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    public void deleteById(Integer id) {
        mesaRepository.deleteById(id);
    }
    public List<Mesa> findMesasDisponiblesExactas(LocalDateTime horaInicio,
                                                  LocalDateTime horaFin,
                                                  Integer numPersonas) {
        return mesaRepository.findMesasDisponiblesExactas(horaInicio, horaFin, numPersonas);
    }
}