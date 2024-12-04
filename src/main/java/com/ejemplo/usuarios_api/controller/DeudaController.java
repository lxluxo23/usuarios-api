package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deudas")
public class DeudaController {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Deuda> obtenerDeudas() {
        return deudaRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Deuda crearDeuda(@RequestBody Deuda deuda) {
        // Validar que el cliente asociado a la deuda existe
        if (deuda.getCliente() == null || deuda.getCliente().getClienteId() == null) {
            throw new IllegalArgumentException("El cliente asociado a la deuda es requerido.");
        }

        // Buscar el cliente en la base de datos
        Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

        // Asignar el cliente encontrado a la deuda
        deuda.setCliente(cliente);

        // Guardar la deuda
        return deudaRepository.save(deuda);
    }
    @GetMapping("/{clienteId}/deudas")
    public List<Deuda> obtenerDeudasPorCliente(@PathVariable Long clienteId) {
        return deudaRepository.findByClienteClienteId(clienteId);
    }
}
