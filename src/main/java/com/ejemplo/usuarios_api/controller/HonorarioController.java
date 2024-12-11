package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("montoMensual") BigDecimal montoMensual,
            @RequestParam(value = "mesesPagados", required = false) List<MultipartFile> mesesPagados,
            @RequestParam Map<String, String> allParams) {

        try {
            // Verificar si el cliente existe
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // Verificar si ya existe un honorario contable para el año actual
            int anioActual = LocalDate.now().getYear();
            List<HonorarioContable> honorariosExistentes = honorarioRepository.findByCliente_ClienteIdAndAnio(clienteId, anioActual);
            if (!honorariosExistentes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Ya existe un honorario contable para este año."));
            }

            // Crear el honorario contable
            HonorarioContable nuevoHonorario = new HonorarioContable();
            nuevoHonorario.setCliente(cliente);
            nuevoHonorario.setMontoMensual(montoMensual);
            nuevoHonorario.setMontoTotal(montoMensual.multiply(BigDecimal.valueOf(12)));
            nuevoHonorario.setMontoPagado(BigDecimal.ZERO);
            nuevoHonorario.setEstado(EstadoDeuda.Pendiente);
            nuevoHonorario.setAnio(anioActual);
            nuevoHonorario.setFechaInicio(LocalDate.of(anioActual, 1, 1)); // Siempre enero
            HonorarioContable honorarioGuardado = honorarioRepository.save(nuevoHonorario);

            // Crear los 12 meses con estado inicial Pendiente
            List<MesHonorario> mesesHonorarios = new ArrayList<>();
            for (int mes = 1; mes <= 12; mes++) {
                MesHonorario mesHonorario = new MesHonorario();
                mesHonorario.setHonorario(honorarioGuardado);
                mesHonorario.setMes(mes);
                mesHonorario.setMontoMensual(montoMensual);
                mesHonorario.setMontoPagado(BigDecimal.ZERO);
                mesHonorario.setEstado(EstadoDeuda.Pendiente);
                mesHonorarioRepository.save(mesHonorario);
                mesesHonorarios.add(mesHonorario);
            }

            // Preparar archivos por mes (si existen)
            Map<Integer, MultipartFile> archivosPorMes = new HashMap<>();
            if (mesesPagados != null) {
                for (MultipartFile archivo : mesesPagados) {
                    String nombreArchivo = archivo.getOriginalFilename();
                    if (nombreArchivo != null) {
                        String[] partes = nombreArchivo.split("_", 2);
                        if (partes.length > 1) {
                            int mesNumerico = Integer.parseInt(partes[0]);
                            archivosPorMes.put(mesNumerico, archivo);
                        }
                    }
                }
            }

            // Procesar pagos
            BigDecimal totalPagado = BigDecimal.ZERO;

            for (int mes = 1; mes <= 12; mes++) {
                MesHonorario mesHonorario = mesesHonorarios.get(mes - 1);

                // Buscar si hay un monto pagado para este mes
                String claveMonto = "montoPagado[" + mes + "]";
                BigDecimal montoPagadoMes = BigDecimal.ZERO;

                if (allParams.containsKey(claveMonto)) {
                    try {
                        String valor = allParams.get(claveMonto);
                        if (valor != null && !valor.trim().isEmpty()) {
                            montoPagadoMes = new BigDecimal(valor);
                        }
                    } catch (NumberFormatException e) {
                        // Si el monto no es válido, ignoramos este mes
                        continue;
                    }
                }

                if (montoPagadoMes.compareTo(BigDecimal.ZERO) > 0) {
                    // Registrar el pago
                    PagoHonorario pagoHonorario = new PagoHonorario();
                    pagoHonorario.setMesHonorario(mesHonorario);
                    pagoHonorario.setMonto(montoPagadoMes);
                    pagoHonorario.setFechaPago(LocalDate.now());

                    // Asociar comprobante si existe
                    if (archivosPorMes.containsKey(mes)) {
                        MultipartFile archivoMes = archivosPorMes.get(mes);
                        pagoHonorario.setComprobante(archivoMes.getOriginalFilename());
                    }

                    pagoHonorarioRepository.save(pagoHonorario);

                    // Actualizar monto pagado del mes
                    mesHonorario.setMontoPagado(mesHonorario.getMontoPagado().add(montoPagadoMes));

                    // Actualizar estado del mes
                    if (mesHonorario.getMontoPagado().compareTo(mesHonorario.getMontoMensual()) >= 0) {
                        mesHonorario.setEstado(EstadoDeuda.Pagado);
                    } else {
                        mesHonorario.setEstado(EstadoDeuda.Pendiente); // Se mantiene pendiente si no está completamente pagado
                    }

                    mesHonorarioRepository.save(mesHonorario);

                    // Sumar al total pagado del honorario
                    totalPagado = totalPagado.add(montoPagadoMes);
                }
            }

            // Actualizar el honorario con el total pagado
            honorarioGuardado.setMontoPagado(totalPagado);

            // Verificar si todos los meses están pagados
            boolean todosPagados = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioGuardado.getHonorarioId())
                    .stream()
                    .allMatch(m -> m.getEstado() == EstadoDeuda.Pagado);

            if (todosPagados) {
                honorarioGuardado.setEstado(EstadoDeuda.Pagado);
            }

            honorarioRepository.save(honorarioGuardado);

            return ResponseEntity.ok(Collections.singletonMap("message", "Honorario contable creado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al crear el honorario contable: " + e.getMessage()));
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

            // Lista de meses
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

            // Respuesta
            respuesta.put("honorario", honorarioInfo);
            respuesta.put("meses", mesesSimplificados);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener detalles: " + e.getMessage());
        }
    }
}
