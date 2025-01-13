package com.ejemplo.usuarios_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ClienteSaldoPendienteDTO {
    private Long clienteId;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private BigDecimal saldoPendiente;

    // Getters y Setters
}

