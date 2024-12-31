package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class HonorarioContableDTO {
    private Long honorarioId;
    private BigDecimal montoMensual;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal montoRestante; // Campo derivado calculado
    private String estado;
    private int anio;
    private LocalDate fechaInicio;
    private Long clienteId;
    private List<MesHonorarioDTO> meses;

    // Constructor
    public HonorarioContableDTO(
            Long honorarioId, BigDecimal montoMensual, BigDecimal montoTotal,
            BigDecimal montoPagado, String estado,
            int anio, LocalDate fechaInicio, Long clienteId, List<MesHonorarioDTO> meses) {
        this.honorarioId = honorarioId;
        this.montoMensual = montoMensual;
        this.montoTotal = montoTotal;
        this.montoPagado = montoPagado;
        this.montoRestante = montoTotal.subtract(montoPagado); // Calculado automáticamente
        this.estado = estado;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.clienteId = clienteId;
        this.meses = meses;
    }

    // Getters y Setters
    public Long getHonorarioId() {
        return honorarioId;
    }

    public void setHonorarioId(Long honorarioId) {
        this.honorarioId = honorarioId;
    }

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
        recalculateMontoRestante(); // Recalcular cuando montoTotal cambie
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
        recalculateMontoRestante(); // Recalcular cuando montoPagado cambie
    }

    public BigDecimal getMontoRestante() {
        return montoRestante;
    }

    public void setMontoRestante(BigDecimal montoRestante) {
        this.montoRestante = montoRestante;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<MesHonorarioDTO> getMeses() {
        return meses;
    }

    public void setMeses(List<MesHonorarioDTO> meses) {
        this.meses = meses;
    }

    // Método auxiliar para recalcular montoRestante
    private void recalculateMontoRestante() {
        if (montoTotal != null && montoPagado != null) {
            this.montoRestante = montoTotal.subtract(montoPagado);
        }
    }
}
