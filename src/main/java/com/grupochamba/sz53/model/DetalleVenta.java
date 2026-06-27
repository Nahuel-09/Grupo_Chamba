package com.grupochamba.sz53.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Venta venta;

    @ManyToOne
    private Producto producto;

    private Integer cantidad;
    private Double precioUnitario;

    private Double gravada = 0.0;
    private Double iva = 0.0;
    private Double subtotal = 0.0;

    public DetalleVenta() {
    }

    public DetalleVenta(Producto producto, Integer cantidad, Double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularImportes();
    }

    public void calcularImportes() {

        if (cantidad == null || precioUnitario == null) {
            subtotal = 0.0;
            iva = 0.0;
            gravada = 0.0;
            return;
        }

        subtotal = cantidad * precioUnitario;
        iva = subtotal / 11;
        gravada = subtotal - iva;
    }

    public Long getId() {
        return id;
    }

    public Venta getVenta() {
        return venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public Double getGravada() {
        return gravada;
    }

    public Double getIva() {
        return iva;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setGravada(Double gravada) {
        this.gravada = gravada;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}