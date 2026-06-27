package com.grupochamba.sz53.model;

import jakarta.persistence.*;

import java.time.*;
import java.util.*;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    private Cliente cliente;

    private Integer procesado = 0;

    private Double totalGravada = 0.0;
    private Double totalIva = 0.0;
    private Double totalVenta = 0.0;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
        this.fecha = LocalDate.now();
        this.procesado = 0;
    }

    public boolean estaProcesada() {
        return procesado != null && procesado == 1;
    }

    public boolean estaNueva() {
        return procesado == null || procesado == 0;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalle.setVenta(this);
        detalles.add(detalle);
    }

    public void calcularTotales() {
        totalGravada = 0.0;
        totalIva = 0.0;
        totalVenta = 0.0;

        for (DetalleVenta detalle : detalles) {
            detalle.calcularImportes();
            totalGravada += detalle.getGravada();
            totalIva += detalle.getIva();
            totalVenta += detalle.getSubtotal();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Integer getProcesado() {
        return procesado;
    }

    public Double getTotalGravada() {
        return totalGravada;
    }

    public Double getTotalIva() {
        return totalIva;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setProcesado(Integer procesado) {
        this.procesado = procesado;
    }

    public void setTotalGravada(Double totalGravada) {
        this.totalGravada = totalGravada;
    }

    public void setTotalIva(Double totalIva) {
        this.totalIva = totalIva;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}