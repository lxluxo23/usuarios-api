package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private BigDecimal montoMensual = BigDecimal.ZERO;

    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDeuda estado;

    @OneToMany(mappedBy = "mesHonorario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoHonorario> pagos = new ArrayList<>(); // Relación con PagoHonorario

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

    public void setHonorario(HonorarioContable honorario) {
        this.honorario = honorario;
    }

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

    public EstadoDeuda getEstado() {
        return estado;
    }

    public void setEstado(EstadoDeuda estado) {
        this.estado = estado;
    }

    public List<PagoHonorario> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoHonorario> pagos) {
        this.pagos = pagos != null ? pagos : new ArrayList<>();
    }

    public void agregarPago(PagoHonorario pago) {
        this.pagos.add(pago);
        pago.setMesHonorario(this); // Relación bidireccional
    }

    public void removerPago(PagoHonorario pago) {
        this.pagos.remove(pago);
        pago.setMesHonorario(null); // Rompe la relación bidireccional
    }

    public void calcularEstado() {
        this.estado = montoPagado.compareTo(montoMensual) >= 0 ? EstadoDeuda.Pagado : EstadoDeuda.Pendiente;
    }
}
