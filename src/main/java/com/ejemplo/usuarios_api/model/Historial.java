package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Marca esta clase como una entidad JPA, asociada a una tabla en la base de datos
@Table(name = "historial") // Define el nombre de la tabla como "historial"
public class Historial {

    @Id // Indica que este campo es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // El valor será generado automáticamente por la base de datos
    @Column(name = "historial_id") // Asocia este campo con la columna "historial_id" en la tabla
    private Long historialId;

    @ManyToOne // Define una relación de muchos a uno con la entidad Deuda
    @JoinColumn(name = "deuda_id") // Especifica la columna "deuda_id" como clave foránea
    private Deuda deuda; // Referencia a la deuda asociada con este historial

    @ManyToOne // Define una relación de muchos a uno con la entidad Pago
    @JoinColumn(name = "pago_id") // Especifica la columna "pago_id" como clave foránea
    private Pago pago; // Referencia al pago asociado con este historial

    @Column(name = "fecha_cambio") // Asocia este campo con la columna "fecha_cambio" en la tabla
    private LocalDateTime fechaCambio; // Fecha y hora en que ocurrió el cambio registrado en el historial

    private String descripcion; // Descripción del evento registrado en el historial

    // Getters y Setters para acceder y modificar los campos

    public Long getHistorialId() {
        return historialId;
    }

    public void setHistorialId(Long historialId) {
        this.historialId = historialId;
    }

    public Deuda getDeuda() {
        return deuda;
    }

    public void setDeuda(Deuda deuda) {
        this.deuda = deuda;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
