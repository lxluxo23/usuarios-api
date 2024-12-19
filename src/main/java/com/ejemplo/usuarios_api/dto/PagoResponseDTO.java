// PagoResponseDTO.java
package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoResponseDTO {
    private Long pagoId;
    private Long deudaId;
    private LocalDate fechaTransaccion;
    private BigDecimal monto;
    private String metodoPago;
    private String observaciones;
    private Integer mes;
    private DeudaSimpleDTO deuda;

    // Constructor completo
    public PagoResponseDTO(Long pagoId, Long deudaId, LocalDate fechaTransaccion,
                           BigDecimal monto, String metodoPago, String observaciones, Integer mes,
                           DeudaSimpleDTO deuda) {
        this.pagoId = pagoId;
        this.deudaId = deudaId;
        this.fechaTransaccion = fechaTransaccion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.mes = mes;
        this.deuda = deuda;
    }

    // Getters y Setters
    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
    }

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

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public DeudaSimpleDTO getDeuda() {
        return deuda;
    }

    public void setDeuda(DeudaSimpleDTO deuda) {
        this.deuda = deuda;
    }
}
