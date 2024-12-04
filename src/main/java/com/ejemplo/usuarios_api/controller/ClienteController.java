package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private PagoRepository pagoRepository;

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
    // Obtener deudas por ID
    @GetMapping("/{clienteId}/deudas")
    public List<Deuda> obtenerDeudasPorCliente(@PathVariable Long clienteId) {
        return deudaRepository.findByCliente_ClienteId(clienteId);
    }

    // Endpoint para obtener todos los pagos de un cliente
    @GetMapping("/{clienteId}/pagos")
    public List<Pago> obtenerPagosPorCliente(@PathVariable Long clienteId) {
        return pagoRepository.findPagosByClienteId(clienteId);
    }

    // Obtener datos completos de un cliente para el dashboard
    @GetMapping("/{clienteId}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Deuda> deudas = deudaRepository.findByCliente_ClienteId(clienteId);

            // Datos del cliente
            Map<String, Object> response = new HashMap<>();
            response.put("summary", Map.of(
                    "nombre", cliente.getNombre(),
                    "rut", cliente.getRut(),
                    "email", cliente.getEmail(),
                    "telefono", cliente.getTelefono(),
                    "direccion", cliente.getDireccion()
            ));

            // Indicadores
            BigDecimal totalPayments = deudas.stream()
                    .flatMap(deuda -> pagoRepository.findByDeudaDeudaId(deuda.getDeudaId()).stream())
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDebt = deudas.stream()
                    .map(Deuda::getMontoRestante)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentMonthDebt = deudas.stream()
                    .filter(deuda -> {
                        LocalDate vencimiento = deuda.getFechaVencimiento();
                        LocalDate hoy = LocalDate.now();
                        return vencimiento.getMonth() == hoy.getMonth() && vencimiento.getYear() == hoy.getYear();
                    })
                    .map(Deuda::getMontoRestante)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.put("indicators", Map.of(
                    "totalPayments", totalPayments,
                    "totalDebt", totalDebt,
                    "currentMonthDebt", currentMonthDebt,
                    "lastTransaction", Map.of("date", "Sin datos", "amount", 0)
            ));

            // Movimientos
            response.put("movements", deudas.stream()
                    .flatMap(deuda -> pagoRepository.findByDeudaDeudaId(deuda.getDeudaId()).stream())
                    .map(pago -> Map.of(
                            "date", pago.getFechaTransaccion() != null ? pago.getFechaTransaccion().toString() : "Sin fecha",
                            "type", "Pago",
                            "amount", pago.getMonto(),
                            "description", pago.getObservaciones() != null ? pago.getObservaciones() : "Sin descripción"
                    ))
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cliente no encontrado"));
        }
    }

}