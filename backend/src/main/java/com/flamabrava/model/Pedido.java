package com.flamabrava.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESPEDTBL")
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPEDID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CCLIID", nullable = false)
    private Cliente cliente;

    @Column(name = "FPEDFECHA", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPedido = new Date();

    @Column(name = "XPEDSTA", length = 20, nullable = false)
    private String estado = "Pendiente";

    @Column(name = "NPEDTOT")
    private BigDecimal total;

    @ManyToMany
    @JoinTable(name = "PEDIDO_PRODUCTO", joinColumns = @JoinColumn(name = "PEDIDO_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCTO_ID"))
    private List<Producto> productos;

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

    public Date getFechaPedido() {
        return fechaPedido;
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
