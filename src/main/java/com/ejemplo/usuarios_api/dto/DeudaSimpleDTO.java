// DeudaSimpleDTO.java
package com.ejemplo.usuarios_api.dto;

public class DeudaSimpleDTO {
    private String tipoDeuda;
    private String observaciones;

    public DeudaSimpleDTO(String tipoDeuda, String observaciones) {
        this.tipoDeuda = tipoDeuda;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public String getTipoDeuda() {
        return tipoDeuda;
    }

    public void setTipoDeuda(String tipoDeuda) {
        this.tipoDeuda = tipoDeuda;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
