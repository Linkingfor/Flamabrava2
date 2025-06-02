package com.flamabrava.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESRESTBL")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // <-- Ajustamos este nombre a "cresid" para que coincida con la tabla real
    @Column(name = "cresid")
    private Integer idReserva;

    // <-- Asegúrate de que el nombre de columna ("ccllid") coincide con tu base de datos
    @Column(name = "ccllid", nullable = false)
    private Integer idUsuario;

    // <-- El JoinColumn debe apuntar a "cmesid", que es la columna real en la BD
    @ManyToOne
    @JoinColumn(name = "cmesid", nullable = false)
    private Mesa mesa;

    // <-- La columna real en tu BD se llama "fresfecha"
    @Column(name = "fresfecha", nullable = false)
    private LocalDateTime fecha;

    // <-- Coincide: "num_personas" en tu tabla
    @Column(name = "num_personas", nullable = false)
    private Integer numPersonas;

    // <-- Si tu tabla actual no tiene estado, puedes omitirlo; si la tuvieras, ajústalo:
    @Column(name = "estado", length = 20)
    private String estado;

    // <-- La columna real se llama "xresobs"
    @Column(name = "xresobs", length = 400)
    private String observaciones;

    // ─── Getters / Setters ───

    public Integer getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Mesa getMesa() {
        return mesa;
    }
    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getNumPersonas() {
        return numPersonas;
    }
    public void setNumPersonas(Integer numPersonas) {
        this.numPersonas = numPersonas;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}