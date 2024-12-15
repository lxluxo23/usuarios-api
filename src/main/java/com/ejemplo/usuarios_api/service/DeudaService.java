package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeudaService {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Convertir Deuda a DeudaDTO
    public DeudaDTO convertirDeudaADeudaDTO(Deuda deuda) {
        return new DeudaDTO(
                deuda.getDeudaId(),
                deuda.getMontoRestante(),
                deuda.getFechaVencimiento(),
                null, // Cliente no incluido en el DTO para evitar referencias cíclicas
                deuda.getTipoDeuda(),
                deuda.getMontoTotal(),
                deuda.getFechaInicio(),
                deuda.getFechaCreacion(),
                deuda.getObservaciones(),
                null, // Pagos pueden incluirse si es necesario
                deuda.getEstadoDeuda()
        );
    }

    // Obtener todas las deudas
    public List<DeudaDTO> obtenerTodasLasDeudas() {
        List<Deuda> deudas = deudaRepository.findAll();
        return deudas.stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    // Crear una nueva deuda
    public Deuda crearDeuda(Deuda deuda) {
        if (deuda.getCliente() == null || deuda.getCliente().getClienteId() == null) {
            throw new IllegalArgumentException("El cliente asociado a la deuda es requerido.");
        }

        Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

        deuda.setCliente(cliente);

        if (deuda.getMontoTotal() == null || deuda.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto total de la deuda debe ser mayor a 0.");
        }

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

    // Obtener deudas por cliente
    public List<DeudaDTO> obtenerDeudasPorCliente(Long clienteId) {
        List<Deuda> deudas = deudaRepository.findAllByClienteClienteId(clienteId);
        return deudas.stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }

    // Obtener deudas pendientes por cliente
    public List<DeudaDTO> obtenerDeudasPendientesPorCliente(Long clienteId) {
        List<Deuda> deudasPendientes = deudaRepository.findAllByClienteClienteId(clienteId).stream()
                .filter(deuda -> deuda.getEstadoDeuda().equals(EstadoDeuda.Pendiente))
                .collect(Collectors.toList());
        return deudasPendientes.stream()
                .map(this::convertirDeudaADeudaDTO)
                .collect(Collectors.toList());
    }
}
