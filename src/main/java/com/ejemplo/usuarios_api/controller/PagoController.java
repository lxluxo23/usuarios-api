package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.PagoDTO;
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
    public ResponseEntity<PagoDTO> crearPago(@PathVariable Long deudaId, @RequestBody PagoDTO pagoDTO) {
        PagoDTO pagoRegistrado = pagoService.registrarPago(deudaId, pagoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoRegistrado);
    }
}
