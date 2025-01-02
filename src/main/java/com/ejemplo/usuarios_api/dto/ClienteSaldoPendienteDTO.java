// src/main/java/com/ejemplo/usuarios_api/dto/ClienteSaldoPendienteDTO.java
package com.ejemplo.usuarios_api.dto;

import java.math.BigDecimal;

public class ClienteSaldoPendienteDTO {
    private Long clienteId;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private BigDecimal saldoPendiente;

    // Constructor
    public ClienteSaldoPendienteDTO(Long clienteId, String nombre, String rut, String email, String telefono, BigDecimal saldoPendiente) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.rut = rut;
        this.email = email;
        this.telefono = telefono;
        this.saldoPendiente = saldoPendiente;
    }

    // Getters y Setters
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
}
