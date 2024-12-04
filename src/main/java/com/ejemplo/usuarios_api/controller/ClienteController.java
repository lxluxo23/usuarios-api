package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todos los clientes
    @GetMapping
    public List<Cliente> obtenerClientes() {
        return clienteRepository.findAll();
    }

    // Crear un nuevo cliente
    @PostMapping
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteRepository.save(cliente);
    }


    // Obtener un cliente por su ID
    @GetMapping("/{clienteId}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // Crear datos simulados o basados en lógica de negocio
            Map<String, Object> response = new HashMap<>();
            response.put("summary", Map.of(
                    "nombre", cliente.getNombre(),
                    "rut", cliente.getRut(),
                    "email", cliente.getEmail(),
                    "telefono", cliente.getTelefono(),
                    "direccion", cliente.getDireccion()
            ));
            response.put("indicators", Map.of(
                    "totalPayments", 5,
                    "totalDebt", 150000,
                    "currentMonthDebt", 50000,
                    "lastTransaction", Map.of("date", "2024-12-01", "amount", 50000)
            ));
            response.put("movements", List.of(
                    Map.of("date", "2024-12-01", "type", "Ingreso", "amount", 50000, "description", "Pago mensual")
            ));
            response.put("alerts", List.of(
                    Map.of("type", "warning", "title", "Pago pendiente", "message", "Tienes una deuda próxima a vencer", "date", "2024-12-10")
            ));

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cliente no encontrado"));
        }
    }

}
