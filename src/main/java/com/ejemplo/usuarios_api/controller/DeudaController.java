package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.service.DeudaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/deudas")
public class DeudaController {

    @Autowired
    private DeudaService deudaService;

    // Obtener todas las deudas
    @GetMapping
    public ResponseEntity<List<DeudaDTO>> obtenerDeudas() {
        List<DeudaDTO> deudas = deudaService.obtenerTodasLasDeudas();
        return ResponseEntity.ok(deudas);
    }

    // Crear una nueva deuda
    @PostMapping
    public ResponseEntity<DeudaDTO> crearDeuda(@RequestBody Deuda deuda) {
        Deuda nuevaDeuda = deudaService.crearDeuda(deuda);
        DeudaDTO deudaDTO = deudaService.convertirDeudaADeudaDTO(nuevaDeuda);
        return ResponseEntity.status(HttpStatus.CREATED).body(deudaDTO);
    }

    // Obtener detalles de una deuda por ID
    @GetMapping("/{deudaId}")
    public ResponseEntity<DeudaDTO> obtenerDeuda(@PathVariable Long deudaId) {
        DeudaDTO deudaDTO = deudaService.obtenerDeudaPorId(deudaId);
        return ResponseEntity.ok(deudaDTO);
    }

    // Obtener deudas por usuario
    @GetMapping("/usuario/{clienteId}")
    public ResponseEntity<List<DeudaDTO>> obtenerDeudasPorUsuario(@PathVariable Long clienteId) {
        List<DeudaDTO> deudas = deudaService.obtenerDeudasPorCliente(clienteId);
        return ResponseEntity.ok(deudas);
    }

    // Obtener deudas pendientes por usuario
    @GetMapping("/usuario/{clienteId}/pendientes")
    public ResponseEntity<List<DeudaDTO>> obtenerDeudasPendientesPorUsuario(@PathVariable Long clienteId) {
        List<DeudaDTO> deudasPendientes = deudaService.obtenerDeudasPendientesPorCliente(clienteId);
        return ResponseEntity.ok(deudasPendientes);
    }
}
