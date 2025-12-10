package com.flamabrava.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "GESRESTBL")
@NamedQueries({
    @NamedQuery(
        name = "Reserva.findBetween",
        query = "SELECT r FROM Reserva r WHERE r.fechaInicio >= :start AND r.fechaFin <= :end"
    )
})
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cresid") // PK
    private Integer id;

    @Transient
    private Cliente cliente;

    @Column(name = "ccllid", nullable = false)
    private Integer idUsuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cmesid", nullable = false)
    private Mesa mesa;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "num_personas", nullable = false)
    private Integer numPersonas;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @Column(name = "xresobs", length = 255)
    private String observaciones;


    // --- Getters y setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
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
        public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
     public Cliente getCliente() {
    return cliente;
  }
}
