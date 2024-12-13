package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
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
@RequestMapping("/api/deudas")
public class DeudaController {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private static final NumberFormat formatoCLP = NumberFormat.getInstance(new Locale("es", "CL"));

    // Obtener todas las deudas
    @GetMapping
    public List<Map<String, Object>> obtenerDeudas() {
        return deudaRepository.findAll().stream().map(deuda -> {
            Map<String, Object> deudaInfo = new HashMap<>();
            deudaInfo.put("deudaId", deuda.getDeudaId());
            deudaInfo.put("cliente", deuda.getCliente().getNombre());
            deudaInfo.put("montoTotal", formatoCLP.format(deuda.getMontoTotal()));
            deudaInfo.put("montoRestante", formatoCLP.format(deuda.getMontoRestante()));
            deudaInfo.put("fechaVencimiento", deuda.getFechaVencimiento() != null ? deuda.getFechaVencimiento().toString() : "Sin fecha");
            deudaInfo.put("fechaCreacion", deuda.getFechaCreacion() != null ? deuda.getFechaCreacion().toString() : "No registrada");
            deudaInfo.put("tipoDeuda", deuda.getTipoDeuda() != null ? deuda.getTipoDeuda() : "Sin tipo");
            deudaInfo.put("observaciones", deuda.getObservaciones() != null ? deuda.getObservaciones() : "Sin observaciones");
            deudaInfo.put("estado", deuda.getEstadoDeuda());
            return deudaInfo;
        }).collect(Collectors.toList());
    }

    // Crear una nueva deuda
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> crearDeuda(@RequestBody Deuda deuda) {
        try {
            if (deuda.getCliente() == null || deuda.getCliente().getClienteId() == null) {
                throw new IllegalArgumentException("El cliente asociado a la deuda es requerido.");
            }

            Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

            deuda.setCliente(cliente);

            if (deuda.getMontoTotal() == null || deuda.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto total de la deuda debe ser mayor a 0.");
            }

            if (deuda.getFechaInicio() == null) {
                throw new IllegalArgumentException("La fecha de inicio es requerida.");
            }

            deuda.setMontoRestante(deuda.getMontoTotal().setScale(2, RoundingMode.HALF_UP));
            deuda.setEstadoDeuda(EstadoDeuda.Pendiente);

            if (deuda.getFechaCreacion() == null) {
                deuda.setFechaCreacion(LocalDate.now());
            }

            Deuda deudaGuardada = deudaRepository.save(deuda);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Deuda creada con éxito.",
                    "deuda", Map.of(
                            "deudaId", deudaGuardada.getDeudaId(),
                            "montoTotal", formatoCLP.format(deudaGuardada.getMontoTotal()),
                            "montoRestante", formatoCLP.format(deudaGuardada.getMontoRestante()),
                            "fechaCreacion", deudaGuardada.getFechaCreacion().toString(),
                            "fechaInicio", deudaGuardada.getFechaInicio().toString(),
                            "fechaVencimiento", deudaGuardada.getFechaVencimiento() != null ? deudaGuardada.getFechaVencimiento().toString() : "Sin fecha",
                            "tipoDeuda", deudaGuardada.getTipoDeuda() != null ? deudaGuardada.getTipoDeuda() : "Sin tipo",
                            "observaciones", deudaGuardada.getObservaciones() != null ? deudaGuardada.getObservaciones() : "Sin observaciones",
                            "estado", deudaGuardada.getEstadoDeuda()
                    )
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al crear la deuda: " + e.getMessage()));
        }
    }

    // Obtener detalles de una deuda por ID
    @GetMapping("/{deudaId}")
    public ResponseEntity<?> obtenerDeuda(@PathVariable Long deudaId) {
        Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId);

        if (deudaOpt.isPresent()) {
            Deuda deuda = deudaOpt.get();
            Map<String, Object> deudaInfo = new HashMap<>();
            deudaInfo.put("deudaId", deuda.getDeudaId());
            deudaInfo.put("cliente", deuda.getCliente().getNombre());
            deudaInfo.put("montoTotal", formatoCLP.format(deuda.getMontoTotal()));
            deudaInfo.put("montoRestante", formatoCLP.format(deuda.getMontoRestante()));
            deudaInfo.put("fechaVencimiento", deuda.getFechaVencimiento() != null ? deuda.getFechaVencimiento().toString() : "Sin fecha");
            deudaInfo.put("fechaCreacion", deuda.getFechaCreacion() != null ? deuda.getFechaCreacion().toString() : "No registrada");
            deudaInfo.put("fechaInicio", deuda.getFechaInicio() != null ? deuda.getFechaInicio().toString() : "No registrada");
            deudaInfo.put("tipoDeuda", deuda.getTipoDeuda() != null ? deuda.getTipoDeuda() : "Sin tipo");
            deudaInfo.put("observaciones", deuda.getObservaciones() != null ? deuda.getObservaciones() : "Sin observaciones");
            deudaInfo.put("estado", deuda.getEstadoDeuda());

            return ResponseEntity.ok(deudaInfo);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Deuda no encontrada."));
        }
    }
    @GetMapping("/usuario/{clienteId}")
    public ResponseEntity<?> obtenerDeudasPorUsuario(@PathVariable Long clienteId) {
        List<Map<String, Object>> deudas = deudaRepository.findAllByClienteClienteId(clienteId).stream().map(deuda -> {
            Map<String, Object> deudaInfo = new HashMap<>();
            deudaInfo.put("deudaId", deuda.getDeudaId());
            deudaInfo.put("cliente", deuda.getCliente().getNombre());
            deudaInfo.put("montoTotal", formatoCLP.format(deuda.getMontoTotal()));
            deudaInfo.put("montoRestante", formatoCLP.format(deuda.getMontoRestante()));
            deudaInfo.put("fechaVencimiento", deuda.getFechaVencimiento() != null ? deuda.getFechaVencimiento().toString() : "Sin fecha");
            deudaInfo.put("fechaCreacion", deuda.getFechaCreacion() != null ? deuda.getFechaCreacion().toString() : "No registrada");
            deudaInfo.put("tipoDeuda", deuda.getTipoDeuda() != null ? deuda.getTipoDeuda() : "Sin tipo");
            deudaInfo.put("observaciones", deuda.getObservaciones() != null ? deuda.getObservaciones() : "Sin observaciones");
            deudaInfo.put("estado", deuda.getEstadoDeuda());
            return deudaInfo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(deudas);
    }
    @GetMapping("/usuario/{clienteId}/pendientes")
    public ResponseEntity<?> obtenerDeudasPendientesPorUsuario(@PathVariable Long clienteId) {
        List<Map<String, Object>> deudasPendientes = deudaRepository.findAllByClienteClienteId(clienteId).stream()
                .filter(deuda -> deuda.getEstadoDeuda().equals(EstadoDeuda.Pendiente)) // Solo deudas pendientes
                .map(deuda -> {
                    Map<String, Object> deudaInfo = new HashMap<>();
                    deudaInfo.put("deudaId", deuda.getDeudaId());
                    deudaInfo.put("descripcion", String.format("%s - Monto restante: %s",
                            deuda.getTipoDeuda(), formatoCLP.format(deuda.getMontoRestante())));
                    return deudaInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(deudasPendientes);
    }


}