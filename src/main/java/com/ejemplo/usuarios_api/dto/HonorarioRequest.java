package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;
import java.util.List;

public class HonorarioRequest {
    private BigDecimal montoMensual;
    private List<MesPago> mesesPagados;
    private int anio;

    // Getters y Setters
    public int getAnio() {
        return anio;
    }
    public void setAnio(int anio) {
        this.anio = anio;
    }
    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public List<MesPago> getMesesPagados() {
        return mesesPagados;
    }

    public void setMesesPagados(List<MesPago> mesesPagados) {
        this.mesesPagados = mesesPagados;
    }

    // Clase estática interna para MesPago
    public static class MesPago {
        private int mes;
        private String comprobante;

        // Getters y Setters
        public int getMes() {
            return mes;
        }

        public void setMes(int mes) {
            this.mes = mes;
        }

        public String getComprobante() {
            return comprobante;
        }

        public void setComprobante(String comprobante) {
            this.comprobante = comprobante;
        }
    }
}
