package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoRequestDTO {
    private LocalDate fechaTransaccion;
    private BigDecimal monto;
    private String metodoPago;
    private String observaciones;

    // Constructor sin parámetros
    public PagoRequestDTO() {}

    // Getters y Setters
    public LocalDate getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDate fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}