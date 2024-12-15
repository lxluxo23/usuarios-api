package com.ejemplo.usuarios_api.service;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    // Convertir Pago a PagoDTO
    public PagoDTO convertirPagoAPagoDTO(Pago pago) {
        return new PagoDTO(
                pago.getPagoId(),
                pago.getDeuda().getDeudaId(),
                pago.getFechaTransaccion(),
                pago.getMonto(),
                pago.getMetodoPago().name(), // Convertir MetodoPago (enum) a String
                pago.getObservaciones(),
                null, // Comprobante puede ser opcional
                pago.getMes()
        );
    }

    // Obtener todos los pagos como DTOs
    public List<PagoDTO> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }

    // Obtener pagos por deuda
    public List<PagoDTO> obtenerPagosPorDeuda(Long deudaId) {
        if (deudaId == null) {
            throw new IllegalArgumentException("El ID de la deuda no puede ser null");
        }

        List<Pago> pagos = pagoRepository.findByDeudaDeudaId(deudaId);

        if (pagos == null || pagos.isEmpty()) {
            return Collections.emptyList();
        }

        return pagos.stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }

    // Registrar un nuevo pago
    public PagoDTO registrarPago(Long deudaId, PagoDTO pagoDTO) {
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + deudaId));

        if (pagoDTO.getMonto() == null || pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a 0.");
        }
        if (pagoDTO.getMonto().compareTo(deuda.getMontoRestante()) > 0) {
            throw new IllegalArgumentException("El monto del pago no puede exceder el monto restante de la deuda.");
        }

        Pago pago = new Pago();
        pago.setDeuda(deuda);
        pago.setMonto(pagoDTO.getMonto());
        pago.setMetodoPago(MetodoPago.valueOf(pagoDTO.getMetodoPago().toUpperCase()));
        pago.setObservaciones(pagoDTO.getObservaciones());
        pago.setFechaTransaccion(pagoDTO.getFechaTransaccion() != null ? pagoDTO.getFechaTransaccion() : LocalDate.now());

        deuda.setMontoRestante(deuda.getMontoRestante().subtract(pagoDTO.getMonto()));

        if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
            deuda.setEstadoDeuda(EstadoDeuda.Pagado);
        }

        deudaRepository.save(deuda);
        Pago pagoGuardado = pagoRepository.save(pago);

        return convertirPagoAPagoDTO(pagoGuardado);
    }

    // Obtener pagos por cliente
    public List<PagoDTO> obtenerPagosPorCliente(Long clienteId) {
        List<Pago> pagos = pagoRepository.findPagosByClienteId(clienteId); // Método JPQL
        return pagos.stream()
                .map(this::convertirPagoAPagoDTO)
                .collect(Collectors.toList());
    }
}
