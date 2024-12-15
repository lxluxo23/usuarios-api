package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoDTO {

    private Long pagoId;
    private Long deudaId; // Asociamos el ID de la deuda
    private LocalDate fechaTransaccion;
    private BigDecimal monto;
    private String metodoPago; // Representamos el Enum como String
    private String observaciones;
    private String comprobanteBase64; // Comprobante como Base64 (opcional)
    private int mes;

    // Constructor completo
    public PagoDTO(Long pagoId, Long deudaId, LocalDate fechaTransaccion, BigDecimal monto, String metodoPago,
                   String observaciones, String comprobanteBase64, int mes) {
        this.pagoId = pagoId;
        this.deudaId = deudaId;
        this.fechaTransaccion = fechaTransaccion;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.observaciones = observaciones;
        this.comprobanteBase64 = comprobanteBase64;
        this.mes = mes;
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

    public String getComprobanteBase64() {
        return comprobanteBase64;
    }

    public void setComprobanteBase64(String comprobanteBase64) {
        this.comprobanteBase64 = comprobanteBase64;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
}
