package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.ejemplo.usuarios_api.model.HonorarioContable;
import com.ejemplo.usuarios_api.model.EstadoDeuda;

@Entity
@Table(name = "mes_honorario")
public class MesHonorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mesId;

    @ManyToOne
    @JoinColumn(name = "honorario_id", nullable = false)
    private HonorarioContable honorario; // Relación con HonorarioContable

    @Column(name = "mes", nullable = false)
    private int mes;

    @Column(name = "monto_mensual", nullable = false)
    private BigDecimal montoMensual;

    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDeuda estado;

    // Getters y Setters
    public Long getMesId() {
        return mesId;
    }

    public void setMesId(Long mesId) {
        this.mesId = mesId;
    }

    public HonorarioContable getHonorario() {
        return honorario;
    }

    public void setHonorario(HonorarioContable honorario) { // Agregar este método
        this.honorario = honorario;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public EstadoDeuda getEstado() {
        return estado;
    }

    public void setEstado(EstadoDeuda estado) {
        this.estado = estado;
    }
}
