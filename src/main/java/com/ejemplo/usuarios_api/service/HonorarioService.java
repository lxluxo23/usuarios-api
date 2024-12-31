package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.HonorarioContableDTO;
import com.ejemplo.usuarios_api.dto.MesHonorarioDTO;
import com.ejemplo.usuarios_api.dto.PagoHonorarioDTO;
import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Object> obtenerComprobante(Long pagoId) {
        PagoHonorario pago = pagoHonorarioRepository.findById(pagoId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));

        if (pago.getComprobante() == null) {
            throw new RuntimeException("El comprobante no está disponible para este pago.");
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("comprobante", pago.getComprobante());
        resultado.put("formato", pago.getFormatoComprobante()); // Recupera el formato del comprobante
        return resultado;
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

    // Registrar un pago para un mes específico con comprobante como BLOB
    public void registrarPago(Long honorarioId, int mes, BigDecimal montoPago, MultipartFile comprobante, LocalDate fechaPagoReal, MetodoPago metodoPago) throws IOException {
        System.out.println("Honorario ID: " + honorarioId);
        System.out.println("Mes: " + mes);
        System.out.println("Monto a pagar: " + montoPago);
        System.out.println("Comprobante recibido: " + comprobante.getOriginalFilename());
        System.out.println("Fecha de pago real: " + fechaPagoReal);
        System.out.println("Método de pago: " + metodoPago);

        // Verificar que el mes exista
        MesHonorario mesHonorario = mesHonorarioRepository.findByHonorario_HonorarioIdAndMes(honorarioId, mes)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El mes " + mes + " no se encuentra asociado al honorario con ID: " + honorarioId));

        BigDecimal montoRestante = mesHonorario.getMontoMensual().subtract(mesHonorario.getMontoPagado());
        System.out.println("Monto restante en el mes: " + montoRestante);

        // Verificar que el monto no exceda el pendiente
        if (montoPago.compareTo(montoRestante) > 0) {
            throw new IllegalArgumentException("El monto del pago excede el monto pendiente para este mes.");
        }

        // Crear un nuevo pago
        PagoHonorario nuevoPago = new PagoHonorario();
        nuevoPago.setMesHonorario(mesHonorario);
        nuevoPago.setMonto(montoPago);
        nuevoPago.setFechaPago(LocalDate.now()); // Fecha automática
        nuevoPago.setFechaPagoReal(fechaPagoReal); // Fecha ingresada por el usuario
        nuevoPago.setMetodoPago(metodoPago); // Método de pago seleccionado
        nuevoPago.setComprobante(comprobante.getBytes());
        nuevoPago.setFormatoComprobante(comprobante.getContentType()); // Establece el formato del archivo

        System.out.println("Comprobante guardado como bytes con tamaño: " + comprobante.getBytes().length);
        System.out.println("Formato del comprobante: " + comprobante.getContentType());

        // Guardar el pago
        pagoHonorarioRepository.save(nuevoPago);

        // Actualizar el estado del mes
        mesHonorario.setMontoPagado(mesHonorario.getMontoPagado().add(montoPago));
        if (mesHonorario.getMontoPagado().compareTo(mesHonorario.getMontoMensual()) >= 0) {
            mesHonorario.setEstado(EstadoDeuda.Pagado);
        }
        mesHonorarioRepository.save(mesHonorario);

        // Actualizar el estado del honorario contable
        HonorarioContable honorario = mesHonorario.getHonorario();
        honorario.setMontoPagado(honorario.getMontoPagado().add(montoPago));

        boolean todosPagados = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId)
                .stream()
                .allMatch(m -> m.getEstado() == EstadoDeuda.Pagado);

        if (todosPagados) {
            honorario.setEstado(EstadoDeuda.Pagado);
        }

        honorarioRepository.save(honorario);
        System.out.println("Pago registrado con éxito.");
    }




    // Eliminar pagos por honorario
    @Transactional
    public void eliminarPagosPorHonorario(Long honorarioId) {
        List<PagoHonorario> pagosHonorarios = pagoHonorarioRepository.findByMesHonorarioHonorarioHonorarioId(honorarioId);
        if (!pagosHonorarios.isEmpty()) {
            pagoHonorarioRepository.deleteAll(pagosHonorarios);
        }
    }

    // Eliminar honorarios por cliente
    @Transactional
    public void eliminarHonorariosPorCliente(Long clienteId) {
        List<HonorarioContable> honorarios = honorarioRepository.findByCliente_ClienteId(clienteId);
        if (!honorarios.isEmpty()) {
            for (HonorarioContable honorario : honorarios) {
                eliminarPagosPorHonorario(honorario.getHonorarioId());
            }
            honorarioRepository.deleteAll(honorarios);
        }
    }

    private HonorarioContableDTO convertirAHonorarioContableDTO(HonorarioContable honorario) {
        List<MesHonorarioDTO> mesesDTO = mesHonorarioRepository.findByHonorario_HonorarioId(honorario.getHonorarioId())
                .stream()
                .map(mes -> new MesHonorarioDTO(
                        mes.getMes(),
                        mes.getMontoMensual(),
                        mes.getMontoPagado(),
                        mes.getEstado().name(),
                        mes.getPagos().stream()
                                .map(pago -> new PagoHonorarioDTO(
                                        pago.getId(),
                                        pago.getFechaPago(),
                                        pago.getFechaPagoReal(),
                                        pago.getMonto(),
                                        pago.getMetodoPago(),
                                        pago.getComprobante() != null ? "Disponible" : "No disponible"
                                ))
                                .collect(Collectors.toList())
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


    public HonorarioContableDTO obtenerDetallesHonorario(Long honorarioId) {
        HonorarioContable honorario = honorarioRepository.findById(honorarioId)
                .orElseThrow(() -> new RuntimeException("Honorario no encontrado con ID: " + honorarioId));

        List<MesHonorarioDTO> mesesDTO = mesHonorarioRepository.findByHonorario_HonorarioId(honorarioId)
                .stream()
                .map(mes -> new MesHonorarioDTO(
                        mes.getMes(),
                        mes.getMontoMensual(),
                        mes.getMontoPagado(),
                        mes.getEstado().name(),
                        mes.getPagos() != null ? mes.getPagos().stream()
                                .map(pago -> new PagoHonorarioDTO(
                                        pago.getId(),
                                        pago.getFechaPago(),
                                        pago.getFechaPagoReal(),
                                        pago.getMonto(),
                                        pago.getMetodoPago(),
                                        pago.getComprobante() != null ? "Disponible" : "No disponible"
                                ))
                                .collect(Collectors.toList()) : Collections.emptyList()
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

    public MesHonorarioDTO obtenerDetalleMes(Long honorarioId, int mes) {
        MesHonorario mesHonorario = mesHonorarioRepository.findByHonorario_HonorarioIdAndMes(honorarioId, mes)
                .orElseThrow(() -> new RuntimeException("No se encontró el mes " + mes + " para el honorario con ID: " + honorarioId));

        // Convertir el MesHonorario a MesHonorarioDTO
        return new MesHonorarioDTO(
                mesHonorario.getMes(),
                mesHonorario.getMontoMensual(),
                mesHonorario.getMontoPagado(),
                mesHonorario.getEstado().name(),
                mesHonorario.getPagos() != null ? mesHonorario.getPagos().stream()
                        .map(pago -> new PagoHonorarioDTO(
                                pago.getId(),
                                pago.getFechaPago(),
                                pago.getFechaPagoReal(),
                                pago.getMonto(),
                                pago.getMetodoPago(),
                                pago.getComprobante() != null ? "Disponible" : "No disponible"
                        ))
                        .collect(Collectors.toList()) : Collections.emptyList()
        );
    }

}
