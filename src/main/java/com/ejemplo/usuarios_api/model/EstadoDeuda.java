package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EstadoDeuda {
    Pendiente,
    Pagado,
    Parcialmente_Pagado,
    Vencido;

    @JsonCreator
    public static EstadoDeuda fromString(String value) {
        return EstadoDeuda.valueOf(value.toUpperCase());
    }
}
