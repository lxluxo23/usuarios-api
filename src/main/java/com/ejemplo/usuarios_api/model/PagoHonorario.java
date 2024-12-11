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

    @Column(name = "comprobante", nullable = false)
    private String comprobante;

    // Getters y Setters
    public Long getId() { // Cambiado de getPagoId() a getId()
        return id;
    }

    public void setId(Long id) { // Cambiado de setPagoId() a setId()
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

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
}
