package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.util.List;

public class MesHonorarioDTO {
    private int mes; // Número del mes (1 a 12)
    private BigDecimal montoMensual; // Monto total asignado al mes
    private BigDecimal montoPagado; // Monto ya pagado
    private String estado; // Estado del mes (Pendiente, Pagado)
    private List<PagoHonorarioDTO> pagos; // Lista de pagos asociados

    public MesHonorarioDTO(int mes, BigDecimal montoMensual, BigDecimal montoPagado, String estado, List<PagoHonorarioDTO> pagos) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El número del mes debe estar entre 1 y 12.");
        }
        this.mes = mes;
        this.montoMensual = montoMensual != null ? montoMensual : BigDecimal.ZERO;
        this.montoPagado = montoPagado != null ? montoPagado : BigDecimal.ZERO;
        this.estado = estado != null ? estado : "Pendiente";
        this.pagos = pagos;
    }

    // Getters y Setters
    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El número del mes debe estar entre 1 y 12.");
        }
        this.mes = mes;
    }

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual != null ? montoMensual : BigDecimal.ZERO;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado != null ? montoPagado : BigDecimal.ZERO;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado != null ? estado : "Pendiente";
    }

    public List<PagoHonorarioDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoHonorarioDTO> pagos) {
        this.pagos = pagos;
    }

    public boolean isCompletamentePagado() {
        return montoPagado.compareTo(montoMensual) >= 0;
    }
}
