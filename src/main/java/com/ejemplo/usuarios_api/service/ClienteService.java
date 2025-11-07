package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.ClienteDTO;
import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DeudaService deudaService;

    @Autowired
    private HonorarioService honorarioService;

    @Autowired
    private PagoService pagoService;

    // Crear un nuevo cliente con validaciones
    public Cliente crearCliente(Cliente cliente) {
        // Validar que el nombre no esté vacío
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        // Validar que el RUT no esté vacío
        if (cliente.getRut() == null || cliente.getRut().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }

        // VALIDACIÓN AGREGADA: Verificar que el RUT no esté duplicado
        if (clienteRepository.existsByRut(cliente.getRut())) {
            throw new IllegalArgumentException("Ya existe un cliente con el RUT: " + cliente.getRut());
        }
        // FIN VALIDACIÓN ✅

        // Guardar cliente en la base de datos
        return clienteRepository.save(cliente);
    }

    // Convertir una entidad Cliente a ClienteDTO
    public ClienteDTO convertirClienteAClienteDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getRut(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getDeudas() != null ? cliente.getDeudas().stream()
                        .map(deuda -> new DeudaDTO(
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
                        ))
                        .collect(Collectors.toList())
                        : List.of() // Si es null, devuelve lista vacía
        );
    }

    // Obtener todos los clientes con paginación y convertirlos a ClienteDTO
    public Page<ClienteDTO> obtenerTodosLosClientes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteRepository.findAll(pageable);
        return clientesPage.map(this::convertirClienteAClienteDTO);
    }

    // Obtener un cliente por ID y convertirlo a ClienteDTO
    public ClienteDTO obtenerClientePorId(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        return convertirClienteAClienteDTO(cliente);
    }

    // Actualizar un cliente
    public Cliente actualizarCliente(Long clienteId, ClienteDTO clienteActualizado) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        // ✅ VALIDACIÓN AGREGADA: Si cambia el RUT, verificar que no esté duplicado
        if (clienteActualizado.getRut() != null &&
                !clienteActualizado.getRut().equals(cliente.getRut())) {
            // El RUT cambió, validar que no exista en otro cliente
            if (clienteRepository.existsByRut(clienteActualizado.getRut())) {
                throw new IllegalArgumentException(
                        "Ya existe otro cliente con el RUT: " + clienteActualizado.getRut()
                );
            }
        }
        // FIN VALIDACIÓN ✅

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setRut(clienteActualizado.getRut());
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setDireccion(clienteActualizado.getDireccion());
        return clienteRepository.save(cliente);
    }

    // Eliminar un cliente
    @Transactional
    public void eliminarCliente(Long clienteId) {
        // Buscar el cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        // Eliminar pagos relacionados con las deudas del cliente
        deudaService.obtenerDeudasPorCliente(clienteId).forEach(deuda -> {
            pagoService.eliminarPagosPorDeuda(deuda.getDeudaId());
        });

        // Eliminar deudas del cliente
        deudaService.eliminarDeudasPorCliente(clienteId);

        // Eliminar pagos honorarios relacionados con los honorarios contables del cliente
        honorarioService.obtenerHonorariosPorCliente(clienteId).forEach(honorario -> {
            honorarioService.eliminarPagosPorHonorario(honorario.getHonorarioId());
        });

        // Eliminar honorarios contables
        honorarioService.eliminarHonorariosPorCliente(clienteId);

        // Eliminar el cliente
        clienteRepository.delete(cliente);
    }

    public List<ClienteSaldoPendienteDTO> obtenerClientesConSaldoPendiente() {
        return clienteRepository.findAllClientesConSaldoPendiente();
    }

    public List<ClienteSaldoPendienteDTO> obtenerClientesConSaldoPendientePorFecha(int mes, int anio) {
        return clienteRepository.findClientesConSaldoPendientePorFecha(mes, anio);
    }
}