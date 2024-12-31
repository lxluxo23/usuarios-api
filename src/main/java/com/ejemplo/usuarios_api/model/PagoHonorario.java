package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class PagoHonorario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_honorario_id") // Debe coincidir con tu base de datos
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mes_honorario_id", nullable = false) // Relacionado con `mes_honorario`
    private MesHonorario mesHonorario;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Lob
    @Column(name = "comprobante", nullable = false)
    private byte[] comprobante; // Cambiado a byte[] para almacenar archivos como BLOB

    @Column(name = "fecha_pago_real", nullable = false)
    private LocalDate fechaPagoReal;

    @Enumerated(EnumType.STRING) // Almacena el valor del enum como texto
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "formato_comprobante", nullable = true)
    private String formatoComprobante; // Almacena el formato del archivo, ej. "image/png"

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MesHonorario getMesHonorario() {
        return mesHonorario;
    }

    public void setMesHonorario(MesHonorario mesHonorario) {
        this.mesHonorario = mesHonorario;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public byte[] getComprobante() {
        return comprobante;
    }

    public void setComprobante(byte[] comprobante) {
        this.comprobante = comprobante;
    }

    public LocalDate getFechaPagoReal() {
        return fechaPagoReal;
    }

    public void setFechaPagoReal(LocalDate fechaPagoReal) {
        this.fechaPagoReal = fechaPagoReal;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getFormatoComprobante() {
        return formatoComprobante;
    }

    public void setFormatoComprobante(String formatoComprobante) {
        this.formatoComprobante = formatoComprobante;
    }
}
