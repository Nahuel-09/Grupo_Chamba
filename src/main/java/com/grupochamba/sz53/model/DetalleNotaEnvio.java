package com.grupochamba.sz53.model;

import jakarta.persistence.*;

@Entity
public class DetalleNotaEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private NotaEnvio notaEnvio;

    @ManyToOne
    private Producto producto;

    private Integer cantidad;

    private Double precio;

    public DetalleNotaEnvio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotaEnvio getNotaEnvio() {
        return notaEnvio;
    }

    public void setNotaEnvio(NotaEnvio notaEnvio) {
        this.notaEnvio = notaEnvio;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Transient
    public Double getSubtotal() {

        if (cantidad == null || precio == null) {
            return 0.0;
        }

        return cantidad * precio;
    }

    @Override
    public String toString() {
        return "DetalleNotaEnvio{" +
                "id=" + id +
                ", producto=" + producto +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                '}';
    }
}