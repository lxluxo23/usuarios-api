package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal; // Para manejar valores monetarios
import java.time.LocalDate; // Para manejar fechas

@Entity // Marca esta clase como una entidad JPA
@Table(name = "pago") // Define el nombre de la tabla en la base de datos como "pago"
public class Pago {

    @Id // Indica que este campo es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del valor para la clave primaria
    @Column(name = "pago_id") // Especifica el nombre de la columna en la base de datos
    private Long pagoId;

    @ManyToOne // Relación de muchos a uno con la entidad Deuda
    @JoinColumn(name = "deuda_id", nullable = false) // Asocia este campo con la columna "deuda_id" como clave foránea
    @JsonBackReference // Evita problemas de serialización recursiva
    private Deuda deuda; // Referencia a la deuda asociada con este pago

    @Column(name = "fecha_transaccion", nullable = false) // Especifica la columna "fecha_transaccion"
    private LocalDate fechaTransaccion; // Fecha en la que se realizó la transacción

    @Column(name = "monto", nullable = false) // Especifica la columna "monto"
    private BigDecimal monto; // Monto del pago realizado

    @Enumerated(EnumType.STRING) // Almacena el valor del enumerado como una cadena de texto en la base de datos
    @Column(name = "estado", nullable = false) // Especifica la columna "estado"
    private EstadoDeuda estado; // Estado del pago: Pagado o Pendiente

    @Enumerated(EnumType.STRING) // Almacena el valor del enumerado como una cadena de texto en la base de datos
    @Column(name = "metodo_pago") // Especifica la columna "metodo_pago"
    private MetodoPago metodoPago; // Metodo de pago (e.g., Tarjeta, Transferencia)

    @Lob // Indica que este campo almacena datos binarios grandes
    private byte[] comprobante; // Comprobante de la transacción (puede ser un archivo escaneado)

    private String observaciones; // Notas adicionales sobre el pago

    @Column(nullable = false)
    private int mes;

    // Getters y Setters para acceder y modificar los campos

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Deuda getDeuda() {
        return deuda;
    }

    public void setDeuda(Deuda deuda) {
        this.deuda = deuda;
    }

    public LocalDate getFechaTransaccion() {
        return fechaTransaccion;
    }

    public void setFechaTransaccion(LocalDate fechaTransaccion) {
        this.fechaTransaccion = fechaTransaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public EstadoDeuda getEstado() {
        return estado;
    }

    public void setEstado(EstadoDeuda estado) {
        this.estado = estado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public byte[] getComprobante() {
        return comprobante;
    }

    public void setComprobante(byte[] comprobante) {
        this.comprobante = comprobante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

}

