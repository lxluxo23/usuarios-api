package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deuda")
public class Deuda {

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

    // Getters y setters
    public Long getDeudaId() {
        return deudaId;
    }

    public void setDeudaId(Long deudaId) {
        this.deudaId = deudaId;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoDeuda getTipoDeuda() {
        return tipoDeuda;
    }

    public void setTipoDeuda(TipoDeuda tipoDeuda) {
        this.tipoDeuda = tipoDeuda;
    }

    public EstadoDeuda getEstadoDeuda() {
        return estadoDeuda;
    }

    public void setEstadoDeuda(EstadoDeuda estadoDeuda) {
        this.estadoDeuda = estadoDeuda;
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

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
}
