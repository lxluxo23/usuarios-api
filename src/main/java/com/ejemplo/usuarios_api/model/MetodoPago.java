package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MetodoPago {
    EFECTIVO,
    TARJETA,
    TRANSFERENCIA, // Asegúrate de que este valor está presente
    CHEQUE
}

