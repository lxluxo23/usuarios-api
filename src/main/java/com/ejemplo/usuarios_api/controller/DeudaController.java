package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.DeudaCompletaDTO;
import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.service.DeudaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
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

    // Actualizar una deuda
    @PutMapping("/{deudaId}")
    public ResponseEntity<DeudaDTO> actualizarDeuda(@PathVariable Long deudaId, @RequestBody Deuda deudaActualizada) {
        Deuda deuda = deudaService.actualizarDeuda(deudaId, deudaActualizada);
        DeudaDTO deudaDTO = deudaService.convertirDeudaADeudaDTO(deuda);
        return ResponseEntity.ok(deudaDTO);
    }

    // Eliminar una deuda
    @DeleteMapping("/{deudaId}")
    public ResponseEntity<Void> eliminarDeuda(@PathVariable Long deudaId) {
        deudaService.eliminarDeuda(deudaId);
        return ResponseEntity.noContent().build();
    }

    // Obtener el total de deudas por usuario
    @GetMapping("/usuario/{clienteId}/total")
    public ResponseEntity<Double> obtenerTotalDeudasPorUsuario(@PathVariable Long clienteId) {
        Double totalDeudas = deudaService.obtenerTotalDeudasPorCliente(clienteId);
        return ResponseEntity.ok(totalDeudas);
    }

    // Filtrar deudas por rango de fechas
    @GetMapping("/filtro-fechas")
    public ResponseEntity<List<DeudaDTO>> obtenerDeudasPorRangoDeFechas(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        List<DeudaDTO> deudas = deudaService.obtenerDeudasPorRangoDeFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(deudas);
    }

    @GetMapping("/{deudaId}/detalle")
    public ResponseEntity<DeudaCompletaDTO> obtenerDeudaConPagos(@PathVariable Long deudaId) {
        DeudaCompletaDTO deudaCompleta = deudaService.obtenerDeudaConPagos(deudaId);
        return ResponseEntity.ok(deudaCompleta);
    }
}