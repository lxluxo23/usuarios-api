package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.DeudaSimpleDTO;
import com.ejemplo.usuarios_api.dto.PagoDTO;
import com.ejemplo.usuarios_api.exception.ResourceNotFoundException;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.MetodoPago;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    // Convertir Pago a PagoDTO incluyendo información simplificada de la deuda
    private PagoDTO convertirPagoAPagoDTO(Pago pago) {
        Deuda deuda = pago.getDeuda();
        DeudaSimpleDTO deudaSimpleDTO = new DeudaSimpleDTO(
                deuda.getTipoDeuda().name(), // Asumiendo que TipoDeuda es un enum
                deuda.getObservaciones()     // Usamos observaciones en lugar de descripcion
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
    public PagoDTO registrarPago(Long deudaId, PagoDTO pagoDTO, byte[] comprobante, String formatoComprobante) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + deudaId));

        // ✅ VALIDACIONES AGREGADAS
        if (pagoDTO.getMonto() == null || pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }

        if (pagoDTO.getMonto().compareTo(deuda.getMontoRestante()) > 0) {
            throw new IllegalArgumentException(
                    "El monto del pago ($" + pagoDTO.getMonto() +
                            ") excede la deuda restante ($" + deuda.getMontoRestante() + ")"
            );
        }
        // FIN VALIDACIONES ✅

        Pago pago = new Pago();
        pago.setDeuda(deuda);
        pago.setMonto(pagoDTO.getMonto());
        pago.setMetodoPago(MetodoPago.valueOf(pagoDTO.getMetodoPago().toUpperCase()));
        pago.setFechaTransaccion(pagoDTO.getFechaTransaccion() != null ? pagoDTO.getFechaTransaccion() : LocalDate.now());
        pago.setObservaciones(pagoDTO.getObservaciones());
        pago.setMes(pagoDTO.getFechaTransaccion().getMonthValue());

        // Guardar el comprobante
        pago.setComprobante(comprobante);
        pago.setFormatoComprobante(formatoComprobante);

        // Actualizar monto restante
        deuda.setMontoRestante(deuda.getMontoRestante().subtract(pagoDTO.getMonto()));

        if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
            deuda.setEstadoDeuda(EstadoDeuda.Pagado);
        }

        deudaRepository.save(deuda);
        Pago pagoGuardado = pagoRepository.save(pago);

        return convertirPagoAPagoDTO(pagoGuardado);
    }


    // Cancelar un pago
    public void cancelarPago(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + pagoId));

        Deuda deuda = pago.getDeuda();
        deuda.setMontoRestante(deuda.getMontoRestante().add(pago.getMonto()));
        deuda.setEstadoDeuda(EstadoDeuda.Pendiente);

        deudaRepository.save(deuda);
        pagoRepository.delete(pago);
    }

    // Obtener pagos por cliente
    public List<PagoDTO> obtenerPagosPorCliente(Long clienteId) {
        return pagoRepository.findPagosByClienteId(clienteId).stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }

    // Obtener deuda ID por pago ID
    public Long obtenerDeudaIdPorPagoId(Long pagoId) {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con ID: " + pagoId));
        return pago.getDeuda().getDeudaId();
    }

    // Obtener todos los pagos
    public List<PagoDTO> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }

    public List<PagoDTO> obtenerPagosPorDeuda(Long deudaId) {
        List<Pago> pagos = pagoRepository.findByDeudaDeudaId(deudaId);
        return pagos.stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }

    public Double obtenerTotalPagosPorDeuda(Long deudaId) {
        return pagoRepository.findByDeudaDeudaId(deudaId).stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    public List<PagoDTO> obtenerPagosPorRangoDeFechas(Long deudaId, String fechaInicio, String fechaFin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inicio = LocalDate.parse(fechaInicio, formatter);
        LocalDate fin = LocalDate.parse(fechaFin, formatter);

        List<Pago> pagos = pagoRepository.findByDeudaDeudaId(deudaId).stream()
                .filter(pago -> !pago.getFechaTransaccion().isBefore(inicio) && !pago.getFechaTransaccion().isAfter(fin))
                .collect(Collectors.toList());

        return pagos.stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }
    public void eliminarPagosPorDeuda(Long deudaId) {
        List<Pago> pagos = pagoRepository.findByDeudaDeudaId(deudaId);
        if (!pagos.isEmpty()) {
            pagoRepository.deleteAll(pagos);
        }
    }

    public Map<String, Object> obtenerComprobante(Long pagoId) {
        Optional<Pago> pagoOpt = pagoRepository.findById(pagoId);
        if (!pagoOpt.isPresent()) {
            System.out.println("Pago no encontrado con ID: " + pagoId);
            return null;
        }

        Pago pago = pagoOpt.get();
        if (pago.getComprobante() == null || pago.getFormatoComprobante() == null) {
            System.out.println("Comprobante o formato es nulo para el pagoId: " + pagoId);
            return null;
        }

        Map<String, Object> comprobanteData = new HashMap<>();
        comprobanteData.put("comprobante", pago.getComprobante());
        comprobanteData.put("formato", pago.getFormatoComprobante());

        System.out.println("Comprobante encontrado para pagoId: " + pagoId);
        return comprobanteData;
    }

}