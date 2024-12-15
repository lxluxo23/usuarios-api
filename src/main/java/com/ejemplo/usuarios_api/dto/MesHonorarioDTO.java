package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;

public class MesHonorarioDTO {
    private int mes;
    private BigDecimal montoMensual;
    private BigDecimal montoPagado;
    private String estado;

    // Constructor
    public MesHonorarioDTO(int mes, BigDecimal montoMensual, BigDecimal montoPagado, String estado) {
        this.mes = mes;
        this.montoMensual = montoMensual;
        this.montoPagado = montoPagado;
        this.estado = estado;
    }

    // Getters y Setters
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }
    public BigDecimal getMontoMensual() { return montoMensual; }
    public void setMontoMensual(BigDecimal montoMensual) { this.montoMensual = montoMensual; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
