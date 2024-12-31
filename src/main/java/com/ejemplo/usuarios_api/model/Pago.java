package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    private Long pagoId;

    @ManyToOne
    @JoinColumn(name = "deuda_id", nullable = false)
    @JsonBackReference // Evita serialización recursiva hacia Deuda
    private Deuda deuda;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDate fechaTransaccion;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Lob
    @Column(name = "comprobante")
    private byte[] comprobante;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "mes", nullable = false)
    private int mes;
}