package com.ejemplo.usuarios_api.dto;

import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoResponseDTO {
    @Setter
    private Long pagoId;
    @Setter
    private Long deudaId;
    private LocalDate fechaTransaccion;
    @Setter
    private BigDecimal monto;
    @Setter
    private String metodoPago;
    @Setter
    private String observaciones;
    @Setter
    private Integer mes;
    @Setter
    private DeudaSimpleDTO deuda;

    // Default constructor (needed for frameworks like Hibernate or Jackson)
    public PagoResponseDTO() {
    }

    // Constructor with all fields
    public PagoResponseDTO(Long pagoId, Long deudaId, LocalDate fechaTransaccion,
                           BigDecimal monto, String metodoPago, String observaciones,
                           Integer mes, DeudaSimpleDTO deuda) {
        this.pagoId = pagoId;
        this.deudaId = deudaId;
        this.fechaTransaccion = fechaTransaccion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.mes = mes;
        this.deuda = deuda;
    }

    // Constructor without 'mes' (computing it from fechaTransaccion if necessary)
    public PagoResponseDTO(Long pagoId, Long deudaId, LocalDate fechaTransaccion,
                           BigDecimal monto, String metodoPago, String observaciones,
                           DeudaSimpleDTO deuda) {
        this.pagoId = pagoId;
        this.deudaId = deudaId;
        this.fechaTransaccion = fechaTransaccion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.mes = (fechaTransaccion != null) ? fechaTransaccion.getMonthValue() : null;
        this.deuda = deuda;
    }

    // Getters and Setters
    public Long getPagoId() {
        return pagoId;
    }

    public Long getDeudaId() {
        return deudaId;
    }

    public LocalDate getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDate fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
        this.mes = (fechaTransaccion != null) ? fechaTransaccion.getMonthValue() : null; // Update mes if fechaTransaccion changes
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Integer getMes() {
        return mes;
    }

    public DeudaSimpleDTO getDeuda() {
        return deuda;
    }

    @Override
    public String toString() {
        return "PagoResponseDTO{" +
                "pagoId=" + pagoId +
                ", deudaId=" + deudaId +
                ", fechaTransaccion=" + fechaTransaccion +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", mes=" + mes +
                ", deuda=" + deuda +
                '}';
    }
}
