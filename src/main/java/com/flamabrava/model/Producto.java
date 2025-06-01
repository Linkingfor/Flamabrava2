package com.flamabrava.model;

import jakarta.persistence.*;
import java.io.Serializable;

import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
@Table(name = "GESPROTBL")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CPROID")
    private Integer id;

    @Column(name = "XPRONOM", length = 100, nullable = false)
    private String nombre;

    @Column(name = "NPROPREC", nullable = false)
    private Double precio;

    @Column(name = "NPROSTK", nullable = false)
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "CCATID")
    private Categoria categoria;

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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
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
