package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.ClienteDTO;
import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.dto.PagoDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.service.ClienteService;
import com.ejemplo.usuarios_api.service.DeudaService;
import com.ejemplo.usuarios_api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DeudaService deudaService;

    @Autowired
    private PagoService pagoService;

    // Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerClientes() {
        List<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(clientes);
    }

    // Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.crearCliente(cliente);
        ClienteDTO clienteDTO = clienteService.convertirClienteAClienteDTO(nuevoCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDTO);
    }

    // Obtener deudas por cliente
    @GetMapping("/{clienteId}/deudas")
    public ResponseEntity<List<DeudaDTO>> obtenerDeudasPorCliente(@PathVariable Long clienteId) {
        List<DeudaDTO> deudas = deudaService.obtenerDeudasPorCliente(clienteId);
        return ResponseEntity.ok(deudas);
    }

    // Obtener pagos por cliente
    @GetMapping("/{clienteId}/pagos")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorCliente(@PathVariable Long clienteId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // Obtener datos completos de un cliente
    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long clienteId) {
        ClienteDTO clienteDTO = clienteService.obtenerClientePorId(clienteId);
        return ResponseEntity.ok(clienteDTO);
    }
}
