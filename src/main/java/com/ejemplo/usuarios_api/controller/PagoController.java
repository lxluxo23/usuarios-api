package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.PagoDTO;
import com.ejemplo.usuarios_api.dto.PagoRequestDTO;
import com.ejemplo.usuarios_api.dto.PagoResponseDTO;
import com.ejemplo.usuarios_api.service.DeudaService;
import com.ejemplo.usuarios_api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private DeudaService deudaService;

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<PagoDTO>> obtenerPagos() {
        List<PagoDTO> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    // Obtener pagos asociados a una deuda específica
    @GetMapping("/deuda/{deudaId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorDeuda(@PathVariable Long deudaId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorDeuda(deudaId);
        return ResponseEntity.ok(pagos);
    }

    // Registrar un nuevo pago asociado a una deuda específica
    @PostMapping("/registrar/{deudaId}")
    public ResponseEntity<PagoResponseDTO> registrarPago(
            @PathVariable Long deudaId,
            @RequestBody PagoRequestDTO pagoRequestDTO) {
        PagoResponseDTO pagoResponseDTO = deudaService.registrarPago(deudaId, pagoRequestDTO);
        return ResponseEntity.ok(pagoResponseDTO);
    }

    // Obtener pagos realizados por un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorCliente(@PathVariable Long clienteId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // Cancelar un pago y ajustar la deuda asociada
    @DeleteMapping("/cancelar/{pagoId}")
    public ResponseEntity<Void> cancelarPago(@PathVariable Long pagoId) {
        Long deudaId = pagoService.obtenerDeudaIdPorPagoId(pagoId);
        pagoService.cancelarPago(pagoId);
        deudaService.actualizarEstadoDeuda(deudaId); // Recalcular el estado de la deuda después de la cancelación
        return ResponseEntity.noContent().build();
    }

    // Obtener el total de pagos realizados por una deuda específica
    @GetMapping("/deuda/{deudaId}/total")
    public ResponseEntity<Double> obtenerTotalPagosPorDeuda(@PathVariable Long deudaId) {
        Double totalPagos = pagoService.obtenerTotalPagosPorDeuda(deudaId);
        return ResponseEntity.ok(totalPagos);
    }

    // Obtener pagos en un rango de fechas para una deuda específica
    @GetMapping("/deuda/{deudaId}/rango-fechas")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorRangoDeFechas(
            @PathVariable Long deudaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorRangoDeFechas(deudaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(pagos);
    }
}
