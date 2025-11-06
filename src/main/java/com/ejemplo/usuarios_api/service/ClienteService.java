package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.ClienteDTO;
import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (cliente.getRut() == null || cliente.getRut().isEmpty()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }

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

    // Obtener todos los clientes y convertirlos a ClienteDTO
    public List<ClienteDTO> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::convertirClienteAClienteDTO)
                .collect(Collectors.toList());
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