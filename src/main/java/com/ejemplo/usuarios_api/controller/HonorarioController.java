package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.HonorarioContableDTO;
import com.ejemplo.usuarios_api.dto.HonorarioRequest;
import com.ejemplo.usuarios_api.service.HonorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/honorarios")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class HonorarioController {

    @Autowired
    private HonorarioService honorarioService;

    // Crear un honorario contable
    @PostMapping("/{clienteId}")
    public ResponseEntity<?> crearHonorarioContable(
            @PathVariable Long clienteId,
            @RequestBody HonorarioRequest honorarioRequest) {
        try {
            honorarioService.crearHonorarioContable(clienteId, honorarioRequest.getMontoMensual());
            return ResponseEntity.ok(Map.of("message", "Honorario contable creado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el honorario contable: " + e.getMessage()));
        }
    }

    // Registrar un pago para un mes específico
    @PostMapping("/{honorarioId}/pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long honorarioId,
            @RequestBody Map<String, Object> request) {
        try {
            int mes = (int) request.get("mes");
            double montoPago = Double.parseDouble(request.get("montoPago").toString());
            String comprobante = (String) request.get("comprobante");

            honorarioService.registrarPago(honorarioId, mes, montoPago, comprobante);

            return ResponseEntity.ok(Map.of("message", "Pago registrado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }

    // Obtener detalles de un honorario contable
    @GetMapping("/{honorarioId}")
    public ResponseEntity<?> obtenerDetallesHonorario(@PathVariable Long honorarioId) {
        try {
            HonorarioContableDTO honorarioDTO = honorarioService.obtenerDetallesHonorario(honorarioId);
            return ResponseEntity.ok(honorarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los detalles: " + e.getMessage()));
        }
    }

    // Obtener honorarios por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<HonorarioContableDTO>> obtenerHonorariosPorCliente(@PathVariable Long clienteId) {
        List<HonorarioContableDTO> honorariosDTO = honorarioService.obtenerHonorariosPorCliente(clienteId);
        return ResponseEntity.ok(honorariosDTO);
    }
}
