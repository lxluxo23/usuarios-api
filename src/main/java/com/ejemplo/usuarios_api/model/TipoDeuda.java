package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDeuda {
    Impuesto("Impuesto"),
    Honorario("Honorario"),
    Honorario_renta_at("Honorario Renta AT"),
    Impuesto_iva("Impuesto IVA"),
    Renta_2024("Renta 2024"),
    Multa("Multa"),
    Imposiciones("Imposiciones");

    private final String value;

    TipoDeuda(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TipoDeuda fromValue(String value) {
        // Convertir el valor recibido a minúsculas y comparar con los valores en el enum
        for (TipoDeuda tipo : TipoDeuda.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        // Lanzar excepción si el valor no se encuentra
        throw new IllegalArgumentException("Valor no válido para TipoDeuda: " + value);
    }
}
