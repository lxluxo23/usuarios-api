package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MetodoPago {
    Tarjeta,
    Transferencia,
    Efectivo,
    Cheque;

    @JsonCreator
    public static MetodoPago fromString(String value) {
        return MetodoPago.valueOf(value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase());
    }
}
