package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deuda")
public class Deuda {

    // Getters y setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deuda_id")
    private Long deudaId;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference // Evita serialización recursiva hacia Cliente
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_deuda", nullable = false)
    private TipoDeuda tipoDeuda;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDeuda estadoDeuda = EstadoDeuda.Pendiente;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "monto_restante")
    private BigDecimal montoRestante;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Column(name = "observaciones")
    private String observaciones;

    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita serialización recursiva hacia Pagos
    private List<Pago> pagos;

}
