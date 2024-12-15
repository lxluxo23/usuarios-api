package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.MovimientoDTO;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.PagoHonorario;
import com.ejemplo.usuarios_api.repository.PagoHonorarioRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PagoHonorarioRepository pagoHonorarioRepository;

    public List<MovimientoDTO> obtenerMovimientos(Long clienteId) {
        // Obtener los pagos normales
        List<MovimientoDTO> pagosNormales = pagoRepository.findByDeudaClienteClienteId(clienteId)
                .stream()
                .map(pago -> new MovimientoDTO(
                        pago.getFechaTransaccion(),
                        "Pago Normal",
                        pago.getMonto(),
                        "Pago asociado a la deuda ID: " + pago.getDeuda().getDeudaId()
                ))
                .collect(Collectors.toList());

        // Obtener los pagos de honorarios
        List<MovimientoDTO> pagosHonorarios = pagoHonorarioRepository.findByMesHonorarioHonorarioClienteClienteId(clienteId)
                .stream()
                .map(pagoHonorario -> new MovimientoDTO(
                        pagoHonorario.getFechaPago(),
                        "Pago Honorario",
                        pagoHonorario.getMonto(),
                        "Pago del mes " + pagoHonorario.getMesHonorario().getMes()
                ))
                .collect(Collectors.toList());

        // Combinar ambas listas
        List<MovimientoDTO> movimientos = new ArrayList<>();
        movimientos.addAll(pagosNormales);
        movimientos.addAll(pagosHonorarios);

        // Ordenar por fecha (descendente)
        movimientos.sort(Comparator.comparing(MovimientoDTO::getFecha).reversed());

        return movimientos;
    }
}
