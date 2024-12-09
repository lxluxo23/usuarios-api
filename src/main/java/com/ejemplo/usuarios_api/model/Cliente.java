package com.ejemplo.usuarios_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity // Marca la clase como una entidad de JPA, representando una tabla en la base de datos
public class Cliente {

    @Id // Indica que este campo es la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autogeneración del valor para la clave primaria
    private Long clienteId;

    private String nombre; // Nombre del cliente
    private String rut; // Identificación única del cliente
    private String direccion; // Dirección del cliente
    private String telefono; // Teléfono del cliente
    private String email; // Email del cliente

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Evita problemas de serialización recursiva en relaciones bidireccionales
    private List<Deuda> deudas; // Lista de deudas asociadas al cliente

    // Getters y Setters para acceder y modificar los campos

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Deuda> getDeudas() {
        return deudas;
    }

    public void setDeudas(List<Deuda> deudas) {
        this.deudas = deudas;
    }
}
