package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PagoHonorarioDTO {
    private Long id; // Identificador único del pago
    private LocalDate fechaPago; // Fecha en la que se registró el pago
    private LocalDate fechaPagoReal; // Fecha real en la que se realizó el pago
    private BigDecimal monto; // Monto del pago
    private MetodoPago metodoPago; // Método de pago utilizado (EFECTIVO, TARJETA, etc.)
    private String comprobante; // Información del comprobante (ruta o descripción)

    /**
     * Constructor completo para inicializar un PagoHonorarioDTO.*
     *
     * @param id            Identificador único del pago.
     * @param fechaPago     Fecha en la que se registró el pago.
     * @param fechaPagoReal Fecha real en la que se realizó el pago.
     * @param monto         Monto del pago.
     * @param metodoPago    Método de pago utilizado.
     * @param comprobante   Información del comprobante.
     */
    public PagoHonorarioDTO(Long id, LocalDate fechaPago, LocalDate fechaPagoReal, BigDecimal monto, MetodoPago metodoPago, String comprobante) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto del pago no puede ser nulo o negativo.");
        }
        this.id = id;
        this.fechaPago = fechaPago != null ? fechaPago : LocalDate.now(); // Por defecto, la fecha actual
        this.fechaPagoReal = fechaPagoReal != null ? fechaPagoReal : LocalDate.now(); // Por defecto, la fecha actual
        this.monto = monto;
        this.metodoPago = metodoPago != null ? metodoPago : MetodoPago.EFECTIVO; // Por defecto, EFECTIVO
        this.comprobante = comprobante != null ? comprobante : "No disponible"; // Por defecto, "No disponible"}
    }
}
