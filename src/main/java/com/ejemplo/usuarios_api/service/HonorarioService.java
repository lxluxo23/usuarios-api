package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.HonorarioContableDTO;
import com.ejemplo.usuarios_api.dto.MesHonorarioDTO;
import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HonorarioService {

    @Autowired
    private HonorarioRepository honorarioRepository;

    @Autowired
    private MesHonorarioRepository mesHonorarioRepository;

    @Autowired
    private PagoHonorarioRepository pagoHonorarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener los detalles de un honorario contable
    public HonorarioContableDTO obtenerDetallesHonorario(Long honorarioId) {
        HonorarioContable honorario = honorarioRepository.findById(honorarioId)
                .orElseThrow(() -> new RuntimeException("Honorario no encontrado con ID: " + honorarioId));

        List<MesHonorarioDTO> mesesDTO = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId)
                .stream()
                .map(mes -> new MesHonorarioDTO(
                        mes.getMes(),
                        mes.getMontoMensual(),
                        mes.getMontoPagado(),
                        mes.getEstado().name()
                ))
                .collect(Collectors.toList());

        return new HonorarioContableDTO(
                honorario.getHonorarioId(),
                honorario.getMontoMensual(),
                honorario.getMontoTotal(),
                honorario.getMontoPagado(),
                honorario.getEstado().name(),
                honorario.getAnio(),
                honorario.getFechaInicio(),
                honorario.getCliente().getClienteId(),
                mesesDTO
        );
    }

    // Obtener todos los honorarios de un cliente
    public List<HonorarioContableDTO> obtenerHonorariosPorCliente(Long clienteId) {
        return honorarioRepository.findByCliente_ClienteId(clienteId)
                .stream()
                .map(this::convertirAHonorarioContableDTO)
                .collect(Collectors.toList());
    }

    // Crear un honorario contable
    @Transactional
    public HonorarioContable crearHonorarioContable(Long clienteId, BigDecimal montoMensual) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        int anioActual = LocalDate.now().getYear();

        // Verificar si ya existe un honorario para este cliente y año
        if (!honorarioRepository.findByCliente_ClienteIdAndAnio(clienteId, anioActual).isEmpty()) {
            throw new RuntimeException("Ya existe un honorario para este año.");
        }

        HonorarioContable honorario = new HonorarioContable();
        honorario.setCliente(cliente);
        honorario.setMontoMensual(montoMensual);
        honorario.setMontoTotal(montoMensual.multiply(BigDecimal.valueOf(12)));
        honorario.setMontoPagado(BigDecimal.ZERO);
        honorario.setEstado(EstadoDeuda.Pendiente);
        honorario.setAnio(anioActual);
        honorario.setFechaInicio(LocalDate.of(anioActual, 1, 1));

        HonorarioContable guardado = honorarioRepository.save(honorario);

        for (int mes = 1; mes <= 12; mes++) {
            MesHonorario mesHonorario = new MesHonorario();
            mesHonorario.setHonorario(guardado);
            mesHonorario.setMes(mes);
            mesHonorario.setMontoMensual(montoMensual);
            mesHonorario.setMontoPagado(BigDecimal.ZERO);
            mesHonorario.setEstado(EstadoDeuda.Pendiente);
            mesHonorarioRepository.save(mesHonorario);
        }

        return guardado;
    }

    // Registrar un pago para un mes específico
    public void registrarPago(Long honorarioId, int mes, double montoPago, String comprobante) {
        // Logs para depuración
        System.out.println("Intentando registrar un pago...");
        System.out.println("Honorario ID: " + honorarioId + ", Mes: " + mes);
        System.out.println("Monto del pago: " + montoPago + ", Comprobante: " + comprobante);

        // Buscar el mes específico para el honorario
        MesHonorario mesHonorario = mesHonorarioRepository.findByHonorario_HonorarioIdAndMes(honorarioId, mes)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El mes " + mes + " no se encuentra asociado al honorario con ID: " + honorarioId));

        // Verificar que el monto del pago no exceda el monto pendiente
        BigDecimal montoRestante = mesHonorario.getMontoMensual().subtract(mesHonorario.getMontoPagado());
        if (BigDecimal.valueOf(montoPago).compareTo(montoRestante) > 0) {
            throw new IllegalArgumentException("El monto del pago excede el monto pendiente para este mes. Monto pendiente: " + montoRestante);
        }

        // Registrar el pago
        PagoHonorario nuevoPago = new PagoHonorario();
        nuevoPago.setMesHonorario(mesHonorario);
        nuevoPago.setMonto(BigDecimal.valueOf(montoPago));
        nuevoPago.setFechaPago(LocalDate.now());
        nuevoPago.setComprobante(comprobante);
        pagoHonorarioRepository.save(nuevoPago);

        System.out.println("Pago registrado en la base de datos.");

        // Actualizar el estado del mes
        mesHonorario.setMontoPagado(mesHonorario.getMontoPagado().add(BigDecimal.valueOf(montoPago)));
        if (mesHonorario.getMontoPagado().compareTo(mesHonorario.getMontoMensual()) >= 0) {
            mesHonorario.setEstado(EstadoDeuda.Pagado);
            System.out.println("El mes " + mes + " ahora está completamente pagado.");
        }
        mesHonorarioRepository.save(mesHonorario);

        // Actualizar el estado del honorario contable
        HonorarioContable honorario = mesHonorario.getHonorario();
        honorario.setMontoPagado(honorario.getMontoPagado().add(BigDecimal.valueOf(montoPago)));

        // Verificar si todos los meses están pagados
        boolean todosPagados = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId)
                .stream()
                .allMatch(m -> m.getEstado() == EstadoDeuda.Pagado);

        if (todosPagados) {
            honorario.setEstado(EstadoDeuda.Pagado);
            System.out.println("El honorario contable ahora está completamente pagado.");
        }

        honorarioRepository.save(honorario);

        System.out.println("Estado del honorario actualizado en la base de datos.");
    }

    // Método para convertir una entidad en un DTO
    private HonorarioContableDTO convertirAHonorarioContableDTO(HonorarioContable honorario) {
        List<MesHonorarioDTO> mesesDTO = mesHonorarioRepository.findByHonorario_HonorarioId(honorario.getHonorarioId())
                .stream()
                .map(mes -> new MesHonorarioDTO(
                        mes.getMes(),
                        mes.getMontoMensual(),
                        mes.getMontoPagado(),
                        mes.getEstado().name()
                ))
                .collect(Collectors.toList());

        return new HonorarioContableDTO(
                honorario.getHonorarioId(),
                honorario.getMontoMensual(),
                honorario.getMontoTotal(),
                honorario.getMontoPagado(),
                honorario.getEstado().name(),
                honorario.getAnio(),
                honorario.getFechaInicio(),
                honorario.getCliente().getClienteId(),
                mesesDTO
        );
    }
    public void eliminarPagosPorHonorario(Long honorarioId) {
        List<PagoHonorario> pagosHonorarios = pagoHonorarioRepository.findByMesHonorarioHonorarioHonorarioId(honorarioId);
        pagoHonorarioRepository.deleteAll(pagosHonorarios);
    }
    @Transactional
    public void eliminarHonorariosPorCliente(Long clienteId) {
        List<HonorarioContable> honorarios = honorarioRepository.findByCliente_ClienteId(clienteId);
        honorarioRepository.deleteAll(honorarios);
    }

}
