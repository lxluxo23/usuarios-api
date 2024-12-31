package com.ejemplo.usuarios_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    @Column(name = "comprobante", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] comprobante; // Cambiado a byte[] para almacenar archivos como BLOB

    @Column(name = "fecha_pago_real", nullable = false)
    private LocalDate fechaPagoReal;

    @Enumerated(EnumType.STRING) // Almacena el valor del enum como texto
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "formato_comprobante", nullable = true)
    private String formatoComprobante; // Almacena el formato del archivo, ej. "image/png
}