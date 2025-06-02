package com.flamabrava.model;

import jakarta.persistence.*;
import java.util.Date;

import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESRESTBL")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CRESID")
    private Integer id;

    @Column(name = "CCLIID")
    private Integer clienteId;

    @ManyToOne
    @JoinColumn(name = "CMESID", nullable = false)
    private Mesa mesa;

    @Column(name = "FRESFECHA", nullable = false)
    private Date fecha;

    @Column(name = "XRESOBS", length = 400)
    private String observaciones;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
