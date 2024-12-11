package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Historial;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.PagoHonorario;
import com.ejemplo.usuarios_api.repository.HistorialRepository;
import com.ejemplo.usuarios_api.repository.PagoHonorarioRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PagoHonorarioRepository pagoHonorarioRepository;

    // Obtener todos los registros del historial y transacciones
    @GetMapping
    public ResponseEntity<?> obtenerHistorialCompleto(
            @RequestParam(required = false) Long deudaId,
            @RequestParam(required = false) Long pagoId,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) LocalDateTime desde,
            @RequestParam(required = false) LocalDateTime hasta) {

        // Obtener historial estándar
        List<Historial> historial = historialRepository.findAll().stream()
                .filter(h -> (deudaId == null || (h.getDeuda() != null && h.getDeuda().getDeudaId().equals(deudaId))))
                .filter(h -> (pagoId == null || (h.getPago() != null && h.getPago().getPagoId().equals(pagoId))))
                .filter(h -> (descripcion == null || h.getDescripcion().toLowerCase().contains(descripcion.toLowerCase())))
                .filter(h -> (desde == null || !h.getFechaCambio().isBefore(desde)))
                .filter(h -> (hasta == null || !h.getFechaCambio().isAfter(hasta)))
                .collect(Collectors.toList());

        // Obtener pagos normales
        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(p -> (desde == null || !p.getFechaPago().isBefore(desde)))
                .filter(p -> (hasta == null || !p.getFechaPago().isAfter(hasta)))
                .collect(Collectors.toList());

        // Obtener pagos contables
        List<PagoHonorario> pagosContables = pagoHonorarioRepository.findAll().stream()
                .filter(p -> (desde == null || !p.getFechaPago().isBefore(desde)))
                .filter(p -> (hasta == null || !p.getFechaPago().isAfter(hasta)))
                .collect(Collectors.toList());

        // Combinar todas las transacciones en una lista unificada
        List<Object> transacciones = new ArrayList<>();
        historial.forEach(h -> transacciones.add(new TransaccionDTO(
                h.getFechaCambio(),
                "Historial",
                h.getDescripcion(),
                h.getMonto()
        )));
        pagos.forEach(p -> transacciones.add(new TransaccionDTO(
                p.getFechaPago(),
                "Pago Normal",
                "Pago de deuda ID: " + p.getDeuda().getDeudaId(),
                p.getMonto()
        )));
        pagosContables.forEach(p -> transacciones.add(new TransaccionDTO(
                p.getFechaPago(),
                "Pago Contable",
                "Pago de honorario ID: " + p.getMesHonorario().getHonorario().getHonorarioId(),
                p.getMonto()
        )));

        // Retornar las transacciones ordenadas por fecha (más recientes primero)
        transacciones.sort((t1, t2) -> ((TransaccionDTO) t2).getFecha().compareTo(((TransaccionDTO) t1).getFecha()));

        return ResponseEntity.ok(transacciones);
    }

    // DTO para representar una transacción
    public static class TransaccionDTO {
        private LocalDateTime fecha;
        private String tipo;
        private String descripcion;
        private BigDecimal monto;

        public TransaccionDTO(LocalDateTime fecha, String tipo, String descripcion, BigDecimal monto) {
            this.fecha = fecha;
            this.tipo = tipo;
            this.descripcion = descripcion;
            this.monto = monto;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public String getTipo() {
            return tipo;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public BigDecimal getMonto() {
            return monto;
        }
    }
}
