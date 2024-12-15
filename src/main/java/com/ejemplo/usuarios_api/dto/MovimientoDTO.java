package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoDTO {
    private LocalDate fecha;
    private String tipo; // "Pago Normal" o "Pago Honorario"
    private BigDecimal monto;
    private String descripcion;

    // Constructor
    public MovimientoDTO(LocalDate fecha, String tipo, BigDecimal monto, String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
