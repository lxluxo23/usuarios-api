package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/deudas")
public class DeudaController {

    @Autowired
    private DeudaRepository deudaRepository; // Inyección del repositorio de deudas

    @Autowired
    private ClienteRepository clienteRepository; // Inyección del repositorio de clientes

    @Autowired
    private PagoRepository pagoRepository; // Inyección de PagoRepository

    // Obtener todas las deudas
    @GetMapping
    public List<Deuda> obtenerDeudas() {
        // Devuelve todas las deudas registradas
        return deudaRepository.findAll();
    }

    // Crear una nueva deuda
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Deuda crearDeuda(@RequestBody Deuda deuda) {
        // Valida que la deuda tenga un cliente asociado
        if (deuda.getCliente() == null || deuda.getCliente().getClienteId() == null) {
            throw new IllegalArgumentException("El cliente asociado a la deuda es requerido.");
        }

        // Busca al cliente asociado a la deuda
        Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

        // Asigna el cliente encontrado a la deuda
        deuda.setCliente(cliente);

        // Guarda la deuda en la base de datos
        return deudaRepository.save(deuda);
    }
}
