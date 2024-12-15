package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.dto.IndicadoresDTO;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IndicadoresService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    public IndicadoresDTO obtenerIndicadores(Long clienteId) {
        // Calcular pagos totales
        List<Pago> pagos = pagoRepository.findByDeudaClienteClienteId(clienteId);
        BigDecimal totalPayments = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular deuda total
        List<Deuda> deudas = deudaRepository.findByClienteClienteId(clienteId);
        BigDecimal totalDebt = deudas.stream()
                .map(Deuda::getMontoRestante)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular deuda del mes actual
        BigDecimal currentMonthDebt = deudas.stream()
                .filter(deuda -> deuda.getFechaVencimiento().getMonthValue() == LocalDate.now().getMonthValue())
                .map(Deuda::getMontoRestante)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener última transacción
        Pago lastPayment = pagos.stream()
                .max((p1, p2) -> p1.getFechaTransaccion().compareTo(p2.getFechaTransaccion()))
                .orElse(null);

        IndicadoresDTO.LastTransactionDTO lastTransaction = lastPayment != null
                ? new IndicadoresDTO.LastTransactionDTO(
                lastPayment.getFechaTransaccion().toString(),
                lastPayment.getMonto())
                : new IndicadoresDTO.LastTransactionDTO("Sin datos", BigDecimal.ZERO);

        // Crear el DTO de respuesta
        return new IndicadoresDTO(totalPayments, totalDebt, currentMonthDebt, lastTransaction);
    }
}
