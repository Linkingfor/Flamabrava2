package com.flamabrava.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESPROTBL")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPROID")
    private Integer id;

    @Column(name = "XPRONOM", length = 100, nullable = false)
    private String nombre;

    // Cambio a BigDecimal para mayor precisión en cálculos monetarios
    @Column(name = "NPROPREC", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "NPROSTK", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCATID")
    private Categoria categoria;

    /** Constructor por defecto requerido por JPA */
    public Producto() {}

    /**
     * Constructor de conveniencia para referenciar solo por ID.
     * Útil en tu servicio/controlador cuando solo necesitas enlazar
     * la FK sin cargar toda la entidad.
     */
    public Producto(Integer id) {
        this.id = id;
    }

    // ————— Getters y setters —————

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
