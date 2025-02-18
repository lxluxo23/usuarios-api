package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/*
 *  ╔═══════════════════════════════════════╗
 *  ║  NOTA: Enum pendiente de mejora       ║
 *  ║  > Los enums deberían ser mayúsculas  ║
 *  ║    Ej: PAGADO en vez de Pagado       ║
 *  ╚═══════════════════════════════════════╝
 */
public enum EstadoDeuda {
    Pendiente,
    Pagado,
    Parcialmente_Pagado,
    Vencido;

    @JsonCreator
    public static EstadoDeuda fromString(String value) {
        for (EstadoDeuda estado : EstadoDeuda.values()) {
            if (estado.name().equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
