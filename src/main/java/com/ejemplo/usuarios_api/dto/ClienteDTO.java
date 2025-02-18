package com.ejemplo.usuarios_api.dto;

import com.ejemplo.usuarios_api.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    private Long clienteId;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;
    private List<DeudaDTO> deudas;

    public ClienteDTO(Long clienteId) {
        this.clienteId = clienteId;
    }

    public ClienteDTO(Cliente model){
        this.clienteId = model.getClienteId();
        this.nombre = model.getNombre();
        this.rut = model.getRut();
        this.email = model.getEmail();
        this.telefono = model.getTelefono();
        this.direccion = model.getDireccion();
        this.deudas = Optional.ofNullable(model.getDeudas()).orElse(List.of()).stream()
                .map(DeudaDTO::new)
                .collect(Collectors.toList());
    }

}
