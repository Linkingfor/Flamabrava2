package com.flamabrava.model;

import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.persistence.*;

@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESMESTBL")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CMESID")
    private Integer id;

    @Column(name = "NMESNUM")
    private Integer numero;

    @Column(name = "NMESCAP")
    private Integer capacidad;

    @Column(name = "ESTADO", nullable = false, length = 10)
    private String estado = "Libre";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
