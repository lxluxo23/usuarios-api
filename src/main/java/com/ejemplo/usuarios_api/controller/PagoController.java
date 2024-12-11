package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    private static final NumberFormat formatoCLP = NumberFormat.getInstance(new Locale("es", "CL"));

    // Obtener todos los pagos
    @GetMapping
    public List<Map<String, Object>> obtenerPagos() {
        return pagoRepository.findAll().stream().map(pago -> {
            Map<String, Object> pagoInfo = new HashMap<>();
            pagoInfo.put("pagoId", pago.getPagoId());
            pagoInfo.put("deudaId", pago.getDeuda().getDeudaId());
            pagoInfo.put("monto", formatoCLP.format(pago.getMonto()));
            pagoInfo.put("fechaTransaccion", pago.getFechaTransaccion() != null ? pago.getFechaTransaccion().toString() : "Sin fecha");
            pagoInfo.put("observaciones", pago.getObservaciones() != null ? pago.getObservaciones() : "Sin observaciones");

            // Agregar resumen de la deuda
            Deuda deuda = pago.getDeuda();
            Map<String, Object> deudaResumen = new HashMap<>();
            deudaResumen.put("montoTotal", formatoCLP.format(deuda.getMontoTotal()));
            deudaResumen.put("montoRestante", formatoCLP.format(deuda.getMontoRestante()));
            deudaResumen.put("estadoDeuda", deuda.getEstadoDeuda().toString());

            pagoInfo.put("resumenDeuda", deudaResumen);

            return pagoInfo;
        }).collect(Collectors.toList());
    }

    // Obtener pagos asociados a una deuda específica
    @GetMapping("/deuda/{deudaId}")
    public ResponseEntity<?> obtenerPagosPorDeuda(@PathVariable Long deudaId) {
        Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId);

        if (deudaOpt.isPresent()) {
            List<Map<String, Object>> pagos = pagoRepository.findByDeudaDeudaId(deudaId).stream().map(pago -> {
                Map<String, Object> pagoInfo = new HashMap<>();
                pagoInfo.put("pagoId", pago.getPagoId());
                pagoInfo.put("monto", formatoCLP.format(pago.getMonto()));
                pagoInfo.put("fechaTransaccion", pago.getFechaTransaccion() != null ? pago.getFechaTransaccion().toString() : "Sin fecha");
                pagoInfo.put("observaciones", pago.getObservaciones() != null ? pago.getObservaciones() : "Sin observaciones");
                return pagoInfo;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(pagos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Deuda no encontrada."));
        }
    }

    // Crear un nuevo pago y asociarlo a una deuda específica
    @PostMapping("/registrar/{deudaId}")
    public ResponseEntity<?> crearPago(@PathVariable Long deudaId, @RequestBody Pago pago) {
        try {
            Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId);

            if (deudaOpt.isEmpty()) {
                throw new IllegalArgumentException("Deuda no encontrada con ID: " + deudaId);
            }

            Deuda deuda = deudaOpt.get();
            pago.setDeuda(deuda);

            if (pago.getMonto() == null || pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor a 0.");
            }

            if (pago.getMonto().compareTo(deuda.getMontoRestante()) > 0) {
                throw new IllegalArgumentException("El monto del pago no puede exceder el monto restante de la deuda.");
            }

            if (pago.getFechaTransaccion() == null) {
                pago.setFechaTransaccion(LocalDate.now());
            }

            BigDecimal nuevoMontoRestante = deuda.getMontoRestante().subtract(pago.getMonto()).setScale(2, RoundingMode.HALF_UP);
            deuda.setMontoRestante(nuevoMontoRestante);

            if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
                deuda.setEstadoDeuda(EstadoDeuda.Pagado);
            }

            deudaRepository.save(deuda);
            Pago pagoGuardado = pagoRepository.save(pago);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Pago registrado con éxito.",
                    "pago", Map.of(
                            "pagoId", pagoGuardado.getPagoId(),
                            "monto", formatoCLP.format(pagoGuardado.getMonto()),
                            "fechaTransaccion", pagoGuardado.getFechaTransaccion().toString(),
                            "observaciones", pagoGuardado.getObservaciones() != null ? pagoGuardado.getObservaciones() : "Sin observaciones"
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }
}
