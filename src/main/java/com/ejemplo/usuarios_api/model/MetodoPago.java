package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MetodoPago {
    TARJETA_CREDITO,
    TRANSFERENCIA,
    EFECTIVO,
    PAYPAL;

    @JsonCreator
    public static EstadoDeuda fromString(String value) {
        return EstadoDeuda.valueOf(value.toUpperCase());
    }
}