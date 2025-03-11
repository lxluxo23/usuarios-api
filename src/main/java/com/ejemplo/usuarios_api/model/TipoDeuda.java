package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDeuda {
    Imposiciones("Imposiciones"),
    Impuesto_iva("Impuesto IVA"),
    Talonarios("Talonarios"),
    Multas("Multas"),
    Impuesto_Renta("Impuesto Renta"),
    Contribuciones("Contribuciones"),
    Otros("Otros"),
    Honorario_contable("Honorario Contable"),
    Imposiciones_nana("Imposiciones Nana"),               // Nuevo tipo
    Imposiciones_independiente("Imposiciones Independiente"); // Nuevo tipo

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
        for (TipoDeuda tipo : TipoDeuda.values()) {
            if (tipo.value.equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor no válido para TipoDeuda: " + value);
    }
}
