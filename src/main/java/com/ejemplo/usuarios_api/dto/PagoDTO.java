// PagoDTO.java
package com.ejemplo.usuarios_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {
    private Long pagoId;
    private Long deudaId;
    private LocalDate fechaTransaccion;
    private BigDecimal monto;
    private String metodoPago;
    private String observaciones;
    private Integer mes;
    private DeudaSimpleDTO deuda; // Información simplificada de la deuda
}