package com.ejemplo.usuarios_api.model;

import com.ejemplo.usuarios_api.dto.ClienteDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clienteId;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Deuda> deudas = new ArrayList<>();

    public Cliente(ClienteDTO dto) {
        this.clienteId = dto.getClienteId();
        this.nombre = dto.getNombre();
        this.rut = dto.getRut();
        this.direccion = dto.getDireccion();
        this.telefono = dto.getTelefono();
        this.email = dto.getEmail();
    }


}
