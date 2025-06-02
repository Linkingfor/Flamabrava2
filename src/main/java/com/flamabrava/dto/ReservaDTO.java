package com.flamabrava.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;


public class ReservaDTO {

    private Integer clienteId;
    private Integer mesaId;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fecha;

    private Integer numPersonas;
    private String observaciones;



    public Integer getClienteId() {
        return clienteId;
    }
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getMesaId() {
        return mesaId;
    }
    public void setMesaId(Integer mesaId) {
        this.mesaId = mesaId;
    }

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getNumPersonas() {
        return numPersonas;
    }
    public void setNumPersonas(Integer numPersonas) {
        this.numPersonas = numPersonas;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
