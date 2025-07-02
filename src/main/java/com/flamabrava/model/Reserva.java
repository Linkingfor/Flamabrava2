package com.flamabrava.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESRESTBL")
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cresid")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccllid", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmesid", nullable = false)
    private Mesa mesa;

    @Column(name = "fresfecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "num_personas", nullable = false)
    private Integer numPersonas;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "xresobs", length = 400)
    private String observaciones;

    // ─── Getters y Setters ───

    public Integer getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Integer idReserva) {
        this.idReserva = idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    // ─── Conveniencias para el ReporteService ───

    /** Para que tu ReporteService pueda usar r.getId() */
    @Transient
    public Integer getId() {
        return this.idReserva;
    }
     @Transient
    public Integer getIdUsuario() {
        return (cliente != null) ? cliente.getId() : null;
    }
     @Transient
    public void setIdUsuario(Integer idUsuario) {
        if (this.cliente == null) {
            this.cliente = new Cliente();
        }
        this.cliente.setId(idUsuario);
    }
}
