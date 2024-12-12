package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.HonorarioRequest;
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
            @RequestBody HonorarioRequest honorarioRequest) {

        try {
            // Extraer datos del cuerpo de la solicitud
            BigDecimal montoMensual = honorarioRequest.getMontoMensual();
            List<HonorarioRequest.MesPago> mesesPagados = honorarioRequest.getMesesPagados();

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

            // Registrar pagos de los meses enviados en la solicitud
            if (mesesPagados != null && !mesesPagados.isEmpty()) {
                for (HonorarioRequest.MesPago mesPago : mesesPagados) {
                    MesHonorario mesHonorario = mesesHonorarios.get(mesPago.getMes() - 1);

                    // Registrar el pago
                    PagoHonorario nuevoPago = new PagoHonorario();
                    nuevoPago.setMesHonorario(mesHonorario);
                    nuevoPago.setMonto(montoMensual); // Aquí puedes ajustar según el monto real pagado si es diferente
                    nuevoPago.setFechaPago(LocalDate.now());
                    nuevoPago.setComprobante(mesPago.getComprobante());
                    pagoHonorarioRepository.save(nuevoPago);

                    // Actualizar el estado del mes
                    mesHonorario.setMontoPagado(montoMensual);
                    mesHonorario.setEstado(EstadoDeuda.Pagado);
                    mesHonorarioRepository.save(mesHonorario);
                }
            }

            // Verificar si todos los meses están pagados
            boolean todosPagados = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioGuardado.getHonorarioId())
                    .stream()
                    .allMatch(m -> m.getEstado() == EstadoDeuda.Pagado);

            if (todosPagados) {
                honorarioGuardado.setEstado(EstadoDeuda.Pagado);
            }

            // Guardar el estado final del honorario
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