package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.EstadoDeuda;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DeudaCompletaDTO {
    private Long deudaId;
    private BigDecimal montoTotal;
    private BigDecimal montoRestante;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private LocalDate fechaCreacion;
    private EstadoDeuda estadoDeuda;
    private String tipoDeuda;
    private String observaciones;
    private List<PagoDTO> pagos;

    public DeudaCompletaDTO() {
    }

    public DeudaCompletaDTO(Long deudaId, BigDecimal montoTotal, BigDecimal montoRestante,
                            LocalDate fechaInicio, LocalDate fechaVencimiento, LocalDate fechaCreacion,
                            EstadoDeuda estadoDeuda, String tipoDeuda, String observaciones, List<PagoDTO> pagos) {
        this.deudaId = deudaId;
        this.montoTotal = montoTotal;
        this.montoRestante = montoRestante;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaCreacion = fechaCreacion;
        this.estadoDeuda = estadoDeuda;
        this.tipoDeuda = tipoDeuda;
        this.observaciones = observaciones;
        this.pagos = pagos;
    }

    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getMontoRestante() {
        return montoRestante;
    }

    public void setMontoRestante(BigDecimal montoRestante) {
        this.montoRestante = montoRestante;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public EstadoDeuda getEstadoDeuda() {
        return estadoDeuda;
    }

    public void setEstadoDeuda(EstadoDeuda estadoDeuda) {
        this.estadoDeuda = estadoDeuda;
    }

    public String getTipoDeuda() {
        return tipoDeuda;
    }

    public void setTipoDeuda(String tipoDeuda) {
        this.tipoDeuda = tipoDeuda;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<PagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoDTO> pagos) {
        this.pagos = pagos;
    }
}
