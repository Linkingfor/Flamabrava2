package com.flamabrava.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "GESPEDTBL")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPEDID")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CCLIID", nullable = false)
    private Cliente cliente;

    @Column(name = "FPEDFECHA", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPedido = new Date();

    @Column(name = "XPEDSTA", length = 20, nullable = false)
    private String estado = "Pendiente";

    @Column(name = "NPEDTOT", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

        @Column(length = 1000)
    private String observaciones;

    /**
     * Campo para almacenar texto con el detalle: "2× Pollo a la brasa, 1× Papas fritas"
     */
    @Column(name = "detalles", length = 500)
    private String detalles;

    @OneToMany(
        mappedBy = "pedido",
        fetch = FetchType.EAGER,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    private List<DetallePedido> detalle = new ArrayList<>();

    public Pedido() {
        // JPA necesita el constructor por defecto
    }

    @Transient
    public Date getFecha() {
        return this.fechaPedido;
    }

    // — Getters y setters —

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public Date getFechaPedido() {
        return fechaPedido;
    }
  public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public List<DetallePedido> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DetallePedido> detalle) {
        this.detalle = detalle;
    }

    /**
     * Recalcula el total a partir de las líneas:
     * total = Σ( cantidad × precioUnitario ).
     */
    public void calcularTotal() {
        this.total = detalle.stream()
            .map(d -> d.getPrecioUnitario()
                       .multiply(BigDecimal.valueOf(d.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}
