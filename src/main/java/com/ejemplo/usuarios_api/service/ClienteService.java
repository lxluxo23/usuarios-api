package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (cliente.getRut() == null || cliente.getRut().isEmpty()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }

        // Guardar cliente en la base de datos
        return clienteRepository.save(cliente);
    }
}
