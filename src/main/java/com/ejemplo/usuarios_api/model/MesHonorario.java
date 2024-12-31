package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}