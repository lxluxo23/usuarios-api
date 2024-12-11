package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Historial;
import com.ejemplo.usuarios_api.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialRepository historialRepository;

    // Obtener todos los registros del historial con filtros opcionales
    @GetMapping
    public ResponseEntity<?> obtenerHistorialConFiltros(
            @RequestParam(required = false) Long deudaId,
            @RequestParam(required = false) Long pagoId,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) LocalDateTime desde,
            @RequestParam(required = false) LocalDateTime hasta) {

        List<Historial> historial = historialRepository.findAll().stream()
                .filter(h -> (deudaId == null || (h.getDeuda() != null && h.getDeuda().getDeudaId().equals(deudaId))))
                .filter(h -> (pagoId == null || (h.getPago() != null && h.getPago().getPagoId().equals(pagoId))))
                .filter(h -> (descripcion == null || h.getDescripcion().toLowerCase().contains(descripcion.toLowerCase())))
                .filter(h -> (desde == null || !h.getFechaCambio().isBefore(desde)))
                .filter(h -> (hasta == null || !h.getFechaCambio().isAfter(hasta)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }

    // Crear un nuevo registro en el historial
    @PostMapping
    public ResponseEntity<Historial> crearHistorial(@RequestBody Historial historial) {
        // Validaciones básicas
        if (historial.getDescripcion() == null || historial.getDescripcion().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        historial.setFechaCambio(LocalDateTime.now());
        Historial nuevoHistorial = historialRepository.save(historial);
        return ResponseEntity.ok(nuevoHistorial);
    }
}
