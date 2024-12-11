package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/honorarios")
public class HonorarioController {

    @Autowired
    private HonorarioRepository honorarioRepository;

    @Autowired
    private MesHonorarioRepository mesHonorarioRepository;

    @Autowired
    private PagoHonorarioRepository pagoHonorarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private static final NumberFormat formatoCLP = NumberFormat.getInstance(new Locale("es", "CL"));

    // Crear un honorario contable
    @PostMapping("/{clienteId}")
    public ResponseEntity<?> crearHonorarioContable(
            @PathVariable Long clienteId,
            @RequestBody Map<String, Object> request) {

        try {
            BigDecimal montoMensual = new BigDecimal(request.get("montoMensual").toString());
            int anioActual = LocalDate.now().getYear();

            // Verificar si ya existe un honorario contable para el cliente en el año actual
            List<HonorarioContable> honorariosExistentes = honorarioRepository.findByCliente_ClienteIdAndAnio(clienteId, anioActual);
            if (!honorariosExistentes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ya existe un honorario contable para este año.");
            }

            // Crear honorario contable
            HonorarioContable nuevoHonorario = new HonorarioContable();
            Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            nuevoHonorario.setCliente(cliente);
            nuevoHonorario.setMontoMensual(montoMensual);
            nuevoHonorario.setMontoTotal(montoMensual.multiply(BigDecimal.valueOf(12)));
            nuevoHonorario.setMontoPagado(BigDecimal.ZERO);
            nuevoHonorario.setEstado(EstadoDeuda.Pendiente);
            nuevoHonorario.setAnio(anioActual);
            nuevoHonorario.setFechaInicio(LocalDate.of(anioActual, 1, 1)); // Asignar la fecha de inicio

            HonorarioContable honorarioGuardado = honorarioRepository.save(nuevoHonorario);

            // Crear 12 meses para el honorario contable
            for (int i = 1; i <= 12; i++) {
                MesHonorario mesHonorario = new MesHonorario();
                mesHonorario.setHonorario(honorarioGuardado);
                mesHonorario.setMes(i);
                mesHonorario.setMontoMensual(montoMensual);
                mesHonorario.setMontoPagado(BigDecimal.ZERO);
                mesHonorario.setEstado(EstadoDeuda.Pendiente);
                mesHonorarioRepository.save(mesHonorario);
            }

            return ResponseEntity.ok("Honorario contable creado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el honorario contable: " + e.getMessage());
        }
    }

    // Registrar un pago para un mes específico
    @PostMapping("/{honorarioId}/pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long honorarioId,
            @RequestBody Map<String, Object> request) {

        try {
            int mes = (int) request.get("mes");
            BigDecimal montoPago = new BigDecimal(request.get("montoPago").toString());
            String comprobante = (String) request.get("comprobante");

            // Buscar el mes correspondiente
            MesHonorario mesHonorario = mesHonorarioRepository.findByHonorario_HonorarioIdAndMes(honorarioId, mes)
                    .orElseThrow(() -> new RuntimeException("Mes no encontrado"));

            // Registrar el pago
            PagoHonorario nuevoPago = new PagoHonorario();
            nuevoPago.setMesHonorario(mesHonorario);
            nuevoPago.setMonto(montoPago);
            nuevoPago.setFechaPago(LocalDate.now());
            nuevoPago.setComprobante(comprobante);
            pagoHonorarioRepository.save(nuevoPago);

            // Actualizar el monto pagado y el estado del mes
            mesHonorario.setMontoPagado(mesHonorario.getMontoPagado().add(montoPago));
            if (mesHonorario.getMontoPagado().compareTo(mesHonorario.getMontoMensual()) >= 0) {
                mesHonorario.setEstado(EstadoDeuda.Pagado);
            }
            mesHonorarioRepository.save(mesHonorario);

            // Actualizar el estado y el monto pagado del honorario contable
            HonorarioContable honorario = mesHonorario.getHonorario();
            honorario.setMontoPagado(honorario.getMontoPagado().add(montoPago));

            boolean todosPagados = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId)
                    .stream()
                    .allMatch(m -> m.getEstado() == EstadoDeuda.Pagado);

            if (todosPagados) {
                honorario.setEstado(EstadoDeuda.Pagado);
            }

            honorarioRepository.save(honorario);

            return ResponseEntity.ok("Pago registrado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el pago: " + e.getMessage());
        }
    }

    // Obtener detalles de un honorario contable
    @GetMapping("/{honorarioId}")
    public ResponseEntity<?> obtenerDetallesHonorario(@PathVariable Long honorarioId) {
        try {
            HonorarioContable honorario = honorarioRepository.findById(honorarioId)
                    .orElseThrow(() -> new RuntimeException("Honorario no encontrado"));

            List<MesHonorario> meses = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId);

            // Construir el mapa de respuesta
            Map<String, Object> respuesta = new HashMap<>();

            // Información del cliente
            Map<String, Object> clienteInfo = new HashMap<>();
            clienteInfo.put("clienteId", honorario.getCliente().getClienteId());
            clienteInfo.put("nombre", honorario.getCliente().getNombre());
            clienteInfo.put("rut", honorario.getCliente().getRut());
            clienteInfo.put("direccion", honorario.getCliente().getDireccion());
            clienteInfo.put("telefono", honorario.getCliente().getTelefono());
            clienteInfo.put("email", honorario.getCliente().getEmail());

            // Información del honorario
            Map<String, Object> honorarioInfo = new HashMap<>();
            honorarioInfo.put("honorarioId", honorario.getHonorarioId());
            honorarioInfo.put("cliente", clienteInfo);
            honorarioInfo.put("montoMensual", formatoCLP.format(honorario.getMontoMensual()));
            honorarioInfo.put("montoTotal", formatoCLP.format(honorario.getMontoTotal()));
            honorarioInfo.put("montoPagado", formatoCLP.format(honorario.getMontoPagado()));
            honorarioInfo.put("estado", honorario.getEstado());
            honorarioInfo.put("anio", honorario.getAnio());
            honorarioInfo.put("fechaInicio", honorario.getFechaInicio());

            // Lista simplificada de meses
            List<Map<String, Object>> mesesSimplificados = meses.stream()
                    .map(mes -> {
                        Map<String, Object> mesInfo = new HashMap<>();
                        mesInfo.put("mesId", mes.getMesId());
                        mesInfo.put("mes", mes.getMes());
                        mesInfo.put("montoMensual", formatoCLP.format(mes.getMontoMensual()));
                        mesInfo.put("montoPagado", formatoCLP.format(mes.getMontoPagado()));
                        mesInfo.put("estado", mes.getEstado());
                        return mesInfo;
                    })
                    .collect(Collectors.toList());

            // Construir la respuesta final
            respuesta.put("honorario", honorarioInfo);
            respuesta.put("meses", mesesSimplificados);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener detalles: " + e.getMessage());
        }
    }
}
