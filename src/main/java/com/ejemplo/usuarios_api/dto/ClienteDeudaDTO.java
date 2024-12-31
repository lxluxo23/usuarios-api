package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;

public class ClienteDeudaDTO {
    private Long clienteId;
    private String clienteNombre;
    private BigDecimal montoDeuda;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private String estado;
    private int mesesPagados; // Número de meses que ha pagado en honorarios contables

    // Constructor con todos los campos
    public ClienteDeudaDTO(Long clienteId, String clienteNombre, BigDecimal montoDeuda, BigDecimal montoPagado, BigDecimal saldoPendiente, String estado, int mesesPagados) {
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.montoDeuda = montoDeuda;
        this.montoPagado = montoPagado;
        this.saldoPendiente = saldoPendiente;
        this.estado = estado;
        this.mesesPagados = mesesPagados;
    }


    // Getters y Setters

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public BigDecimal getMontoDeuda() {
        return montoDeuda;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public int getMesesPagados() {
        return mesesPagados;
    }


    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }


    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }


    public void setMontoDeuda(BigDecimal montoDeuda) {
        this.montoDeuda = montoDeuda;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }


    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }


    public void setEstado(String estado) {
        this.estado = estado;
    }


    public void setMesesPagados(int mesesPagados) {
        this.mesesPagados = mesesPagados;
    }

    public void calcularEstado() {
        if (this.saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
            this.estado = "Al Día";
        } else {
            this.estado = "Pendiente";
        }
    }

}
