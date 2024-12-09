package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal; // Para manejar valores monetarios con precisión
import java.time.LocalDate; // Para manejar fechas
import java.util.List;

@Entity // Marca la clase como una entidad JPA
@Table(name = "deuda") // Asocia la clase con la tabla "deuda" en la base de datos
public class Deuda {

    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autogeneración del valor para la clave primaria
    @Column(name = "deuda_id") // Especifica el nombre de la columna en la base de datos
    private Long deudaId;

    @ManyToOne // Define una relación de muchos a uno con Cliente
    @JoinColumn(name = "cliente_id", nullable = false) // Asocia este campo con la columna "cliente_id"
    @JsonBackReference // Evita problemas de serialización recursiva
    private Cliente cliente; // Cliente asociado a la deuda

    @Enumerated(EnumType.STRING) // Enum almacenado como cadena de texto
    @Column(name = "tipo_deuda", nullable = false) // Especifica la columna "tipo_deuda"
    private TipoDeuda tipoDeuda; // Tipo de deuda (e.g., Hipoteca, Préstamo)

    @Enumerated(EnumType.STRING) // Enum almacenado como cadena de texto
    @Column(name = "estado", nullable = false) // Especifica la columna "estado"
    private EstadoDeuda estadoDeuda = EstadoDeuda.Pendiente; // Estado de la deuda (e.g., Pendiente, Pagada)

    @Column(name = "monto_total", nullable = false) // Especifica la columna "monto_total"
    private BigDecimal montoTotal; // Monto total de la deuda

    @Column(name = "monto_restante") // Especifica la columna "monto_restante"
    private BigDecimal montoRestante; // Monto restante de la deuda

    @Column(name = "fecha_creacion", nullable = false) // Especifica la columna "fecha_creacion"
    private LocalDate fechaCreacion; // Fecha en la que se creó la deuda

    @Column(name = "fecha_inicio", nullable = false) // Especifica la columna "fecha_inicio"
    private LocalDate fechaInicio; // Fecha de inicio de la deuda

    @Column(name = "fecha_vencimiento", nullable = false) // Especifica la columna "fecha_vencimiento"
    private LocalDate fechaVencimiento; // Fecha límite para el pago de la deuda

    @Column(name = "observaciones") // Especifica la columna "observaciones"
    private String observaciones; // Notas adicionales sobre la deuda

    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita problemas de serialización recursiva en relaciones bidireccionales
    private List<Pago> pagos; // Lista de pagos asociados a esta deuda

    // Getters y Setters para acceder y modificar los campos

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
