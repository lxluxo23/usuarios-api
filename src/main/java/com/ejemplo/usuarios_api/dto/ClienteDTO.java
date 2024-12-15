package com.ejemplo.usuarios_api.dto;

import java.util.List;

public class ClienteDTO {

    private Long clienteId;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;
    private List<DeudaDTO> deudas; // Lista de DeudaDTO para representar las deudas del cliente

    // Constructor completo
    public ClienteDTO(Long clienteId, String nombre, String rut, String email, String telefono, String direccion, List<DeudaDTO> deudas) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.rut = rut;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.deudas = deudas;
    }

    // Getters y setters
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<DeudaDTO> getDeudas() {
        return deudas;
    }

    public void setDeudas(List<DeudaDTO> deudas) {
        this.deudas = deudas;
    }
}
