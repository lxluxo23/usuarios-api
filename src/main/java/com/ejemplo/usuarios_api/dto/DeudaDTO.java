package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.TipoDeuda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DeudaDTO {

    private Long deudaId;
    private BigDecimal montoRestante;
    private LocalDate fechaVencimiento;
    private Cliente.ClienteDTO cliente; // Usamos ClienteDTO en lugar de Cliente
    private TipoDeuda tipoDeuda;
    private BigDecimal montoTotal;
    private LocalDate fechaInicio;
    private LocalDate fechaCreacion;
    private String observaciones;
    private List<PagoDTO> pagos; // Lista de Pagos como DTOs
    private EstadoDeuda estadoDeuda;

    // Constructor completo
    public DeudaDTO(Long deudaId, BigDecimal montoRestante, LocalDate fechaVencimiento, Cliente.ClienteDTO cliente,
                    TipoDeuda tipoDeuda, BigDecimal montoTotal, LocalDate fechaInicio, LocalDate fechaCreacion,
                    String observaciones, List<PagoDTO> pagos, EstadoDeuda estadoDeuda) {
        this.deudaId = deudaId;
        this.montoRestante = montoRestante;
        this.fechaVencimiento = fechaVencimiento;
        this.cliente = cliente;
        this.tipoDeuda = tipoDeuda;
        this.montoTotal = montoTotal;
        this.fechaInicio = fechaInicio;
        this.fechaCreacion = fechaCreacion;
        this.observaciones = observaciones;
        this.pagos = pagos;
        this.estadoDeuda = estadoDeuda;
    }

    // Getters y setters
    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
    }

    public BigDecimal getMontoRestante() {
        return montoRestante;
    }

    public void setMontoRestante(BigDecimal montoRestante) {
        this.montoRestante = montoRestante;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Cliente.ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(Cliente.ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public TipoDeuda getTipoDeuda() {
        return tipoDeuda;
    }

    public void setTipoDeuda(TipoDeuda tipoDeuda) {
        this.tipoDeuda = tipoDeuda;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public EstadoDeuda getEstadoDeuda() {
        return estadoDeuda;
    }

    public void setEstadoDeuda(EstadoDeuda estadoDeuda) {
        this.estadoDeuda = estadoDeuda;
    }
}
