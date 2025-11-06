package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.*;
import com.ejemplo.usuarios_api.exception.ResourceNotFoundException;
import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeudaService {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Convertir Deuda a DeudaDTO
    public DeudaDTO convertirDeudaADeudaDTO(Deuda deuda) {
        return new DeudaDTO(
                deuda.getDeudaId(),
                deuda.getMontoRestante(),
                deuda.getFechaVencimiento(),
                null,
                deuda.getTipoDeuda(),
                deuda.getMontoTotal(),
                deuda.getFechaInicio(),
                deuda.getFechaCreacion(),
                deuda.getObservaciones(),
                null,
                deuda.getEstadoDeuda()
        );
    }

    // Obtener todas las deudas
    public List<DeudaDTO> obtenerTodasLasDeudas() {
        return deudaRepository.findAll().stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    // Crear una nueva deuda
    public Deuda crearDeuda(Deuda deuda) {
        Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

        deuda.setCliente(cliente);
        deuda.setMontoRestante(deuda.getMontoTotal());
        deuda.setEstadoDeuda(EstadoDeuda.Pendiente);
        deuda.setFechaCreacion(deuda.getFechaCreacion() != null ? deuda.getFechaCreacion() : LocalDate.now());

        return deudaRepository.save(deuda);
    }

    // Obtener una deuda por ID
    public DeudaDTO obtenerDeudaPorId(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada con ID: " + deudaId));
        return convertirDeudaADeudaDTO(deuda);
    }

    // Actualizar una deuda
    public Deuda actualizarDeuda(Long deudaId, Deuda deudaActualizada) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada con ID: " + deudaId));

        deuda.setMontoTotal(deudaActualizada.getMontoTotal());
        deuda.setMontoRestante(deudaActualizada.getMontoRestante());
        deuda.setTipoDeuda(deudaActualizada.getTipoDeuda());
        deuda.setObservaciones(deudaActualizada.getObservaciones());
        deuda.setEstadoDeuda(deudaActualizada.getEstadoDeuda());

        return deudaRepository.save(deuda);
    }

    // Actualizar el estado de una deuda
    public void actualizarEstadoDeuda(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada con ID: " + deudaId));

        if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
            deuda.setEstadoDeuda(EstadoDeuda.Pagado);
        } else {
            deuda.setEstadoDeuda(EstadoDeuda.Pendiente);
        }

        deudaRepository.save(deuda);
    }

    // Obtener deudas por cliente
    public List<DeudaDTO> obtenerDeudasPorCliente(Long clienteId) {
        return deudaRepository.findAllByClienteClienteId(clienteId).stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    public List<DeudaDTO> obtenerDeudasPendientesPorCliente(Long clienteId) {
        List<Deuda> deudasPendientes = deudaRepository.findAllByClienteClienteId(clienteId).stream()
                .filter(deuda -> deuda.getEstadoDeuda() == EstadoDeuda.Pendiente)
                .collect(Collectors.toList());
        return deudasPendientes.stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    public void eliminarDeuda(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada con ID: " + deudaId));
        deudaRepository.delete(deuda);
    }

    public Double obtenerTotalDeudasPorCliente(Long clienteId) {
        return deudaRepository.findAllByClienteClienteId(clienteId).stream()
                .map(Deuda::getMontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    public List<DeudaDTO> obtenerDeudasPorRangoDeFechas(String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
        LocalDate fin = LocalDate.parse(fechaFin, formatter);

        List<Deuda> deudas = deudaRepository.findAll().stream()
                .filter(deuda -> !deuda.getFechaCreacion().isBefore(inicio) && !deuda.getFechaCreacion().isAfter(fin))
                .collect(Collectors.toList());

        return deudas.stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    public void eliminarDeudasPorCliente(Long clienteId) {
        List<Deuda> deudas = deudaRepository.findAllByClienteClienteId(clienteId);
        if (!deudas.isEmpty()) {
            deudaRepository.deleteAll(deudas);
        }
    }
    public DeudaCompletaDTO obtenerDeudaConPagos(Long deudaId) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + deudaId));

        List<Pago> pagos = pagoRepository.findByDeudaDeudaId(deudaId);
        List<PagoDTO> pagosDTO = pagos.stream().map(this::convertirPagoAPagoDTO).collect(Collectors.toList());

        return new DeudaCompletaDTO(
                deuda.getDeudaId(),
                deuda.getMontoTotal(),
                deuda.getMontoRestante(),
                deuda.getFechaInicio(),
                deuda.getFechaVencimiento(),
                deuda.getFechaCreacion(),
                deuda.getEstadoDeuda(),
                deuda.getTipoDeuda().name(), // Convertimos el enum a String
                deuda.getObservaciones(),
                pagosDTO
        );
    }

    private PagoDTO convertirPagoAPagoDTO(Pago pago) {
        Deuda deuda = pago.getDeuda();
        DeudaSimpleDTO deudaSimpleDTO = new DeudaSimpleDTO(
                deuda.getTipoDeuda().name(),
                deuda.getObservaciones()
        );

        return new PagoDTO(
                pago.getPagoId(),
                deuda.getDeudaId(),
                pago.getFechaTransaccion(),
                pago.getMonto(),
                pago.getMetodoPago().name(),
                pago.getObservaciones(),
                pago.getMes(),
                deudaSimpleDTO
        );
    }

    // Registrar un nuevo pago
    public PagoResponseDTO registrarPago(Long deudaId, PagoRequestDTO pagoRequestDTO) {
        // Buscar la deuda por ID
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + deudaId));

        // ✅ VALIDACIONES AGREGADAS
        if (pagoRequestDTO.getMonto() == null || pagoRequestDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }

        if (pagoRequestDTO.getMonto().compareTo(deuda.getMontoRestante()) > 0) {
            throw new IllegalArgumentException(
                    "El monto del pago ($" + pagoRequestDTO.getMonto() +
                            ") excede la deuda restante ($" + deuda.getMontoRestante() + ")"
            );
        }
        // FIN VALIDACIONES

        // Crear una nueva instancia de Pago
        Pago pago = new Pago();
        pago.setDeuda(deuda);
        pago.setMonto(pagoRequestDTO.getMonto());
        pago.setMetodoPago(MetodoPago.valueOf(pagoRequestDTO.getMetodoPago().toUpperCase()));
        pago.setFechaTransaccion(pagoRequestDTO.getFechaTransaccion() != null ? pagoRequestDTO.getFechaTransaccion() : LocalDate.now());
        pago.setObservaciones(pagoRequestDTO.getObservaciones());

        // Calcular y establecer el mes basado en fechaTransaccion
        System.out.println(pago.toString());
        pago.setMes(pago.getFechaTransaccion().getMonthValue());

        // Actualizar monto restante en la deuda
        deuda.setMontoRestante(deuda.getMontoRestante().subtract(pagoRequestDTO.getMonto()));

        // Actualizar el estado de la deuda si es necesario
        if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
            deuda.setEstadoDeuda(EstadoDeuda.Pagado);
        }

        // Guardar los cambios en la deuda
        deudaRepository.save(deuda);

        // Guardar el pago
        Pago pagoGuardado = pagoRepository.save(pago);

        // Convertir el Pago guardado a PagoResponseDTO para la respuesta
        return convertirPagoAResponseDTO(pagoGuardado);
    }

    private PagoResponseDTO convertirPagoAResponseDTO(Pago pago) {
        Deuda deuda = pago.getDeuda();
        DeudaSimpleDTO deudaSimpleDTO = new DeudaSimpleDTO(
                deuda.getTipoDeuda().name(),
                deuda.getObservaciones()
        );

        return new PagoResponseDTO(
                pago.getPagoId(),
                deuda.getDeudaId(),
                pago.getFechaTransaccion(),
                pago.getMonto(),
                pago.getMetodoPago().name(),
                pago.getObservaciones(),
                pago.getMes(),
                deudaSimpleDTO
        );
    }
}