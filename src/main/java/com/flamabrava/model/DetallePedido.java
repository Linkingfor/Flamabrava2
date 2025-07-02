
package com.flamabrava.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESDETTBL")
public class DetallePedido implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CDETID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CPEDID", nullable = false)
    @JsonBackReference
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CPROID", nullable = false)
    private Producto producto;

    @Column(name = "NDETCNT", nullable = false)
    private Integer cantidad;

    /**
     * Total de la línea (cantidad * precio unitario).
     */
    @Column(name = "NDETTOT", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    // === Constructors ===

    public DetallePedido() {
    }

    // === getters/setters ===

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    // === Lifecycle callbacks ===

    @PrePersist
    @PreUpdate
    private void calcularTotal() {
        if (cantidad != null && producto != null && producto.getPrecio() != null) {
            this.total = producto.getPrecio()
                .multiply(new BigDecimal(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
        }
    }

    // === Transient helpers ===

    /**
     * Precio unitario calculado como total / cantidad.
     */
    @Transient
    public BigDecimal getPrecioUnitario() {
        if (cantidad == null || cantidad == 0 || total == null) {
            return BigDecimal.ZERO;
        }
        return total.divide(new BigDecimal(cantidad), 2, RoundingMode.HALF_UP);
    }

    /**
     * Conveniencia para el subtotal (el total de la línea).
     */
    @Transient
    public BigDecimal getSubtotal() {
        return total;
    }
}
