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
    private ClienteRepository clienteRepository; // Inyeccion del repositorio de clientes

    @Autowired
    private DeudaRepository deudaRepository; // Inyeccion del repositorio de deudas

    @Autowired
    private PagoRepository pagoRepository; // Inyeccion del repositorio de pagos

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
        //Obtener cliente por su ID
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Deuda> deudas = deudaRepository.findByCliente_ClienteId(clienteId);

            // Crear un mapa para estructurar la respuesta
            Map<String, Object> response = new HashMap<>();

            //Agregar informacion basica del cliente
            response.put("summary", Map.of(
                    "nombre", cliente.getNombre(),
                    "rut", cliente.getRut(),
                    "email", cliente.getEmail(),
                    "telefono", cliente.getTelefono(),
                    "direccion", cliente.getDireccion()
            ));

            // Calcular indicadores financieros
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

            // Agregar movimientos (pagos realizados)
            response.put("movements", deudas.stream()
                    .flatMap(deuda -> pagoRepository.findByDeudaDeudaId(deuda.getDeudaId()).stream())
                    .map(pago -> Map.of(
                            "date", pago.getFechaTransaccion() != null ? pago.getFechaTransaccion().toString() : "Sin fecha",
                            "type", "Pago",
                            "amount", pago.getMonto(),
                            "description", pago.getObservaciones() != null ? pago.getObservaciones() : "Sin descripción"
                    ))
                    .collect(Collectors.toList()));

            // Retornar respuesta
            return ResponseEntity.ok(response);
        } else {
            // Retorna error si el cliente no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cliente no encontrado"));
        }
    }
    @GetMapping("/{clienteId}/finanzas")
    public ResponseEntity<Map<String, Object>> obtenerFinanzasPorCliente(@PathVariable Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<Deuda> deudas = deudaRepository.findByCliente_ClienteId(clienteId);
            List<Pago> pagos = pagoRepository.findPagosByClienteId(clienteId);

            // Calcular total pagado
            BigDecimal totalPayments = pagos.stream()
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular deuda total
            BigDecimal totalDebt = deudas.stream()
                    .map(Deuda::getMontoRestante)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calcular pagos pendientes (deudas no pagadas)
            long pendingPayments = deudas.stream()
                    .filter(deuda -> deuda.getMontoRestante().compareTo(BigDecimal.ZERO) > 0)
                    .count();

            // Determinar próxima fecha de vencimiento
            LocalDate nextDueDate = deudas.stream()
                    .map(Deuda::getFechaVencimiento)
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            // Crear respuesta consolidada
            Map<String, Object> response = new HashMap<>();
            response.put("totalPayments", totalPayments);
            response.put("totalDebt", totalDebt);
            response.put("pendingPayments", pendingPayments);
            response.put("nextDueDate", nextDueDate != null ? nextDueDate.toString() : "Sin fechas próximas");
            response.put("movements", pagos.stream()
                    .map(pago -> Map.of(
                            "amount", pago.getMonto(),
                            "date", pago.getFechaTransaccion() != null ? pago.getFechaTransaccion().toString() : "Sin fecha",
                            "description", pago.getObservaciones() != null ? pago.getObservaciones() : "Sin descripción"
                    ))
                    .collect(Collectors.toList())
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Cliente no encontrado"));
        }
    }


}