package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    public Pago registrarPago(Long deudaId, Pago pago) {
        // Verifica si la deuda existe
        Deuda deuda = deudaRepository.findById(deudaId)
                .orElseThrow(() -> new IllegalArgumentException("Deuda no encontrada"));

        // Asocia la deuda con el pago
        pago.setDeuda(deuda);
        pago.setFechaTransaccion(LocalDate.now());  // Asigna la fecha actual si es necesario

        // Asegúrate de que el monto esté usando BigDecimal para evitar problemas con decimales
        BigDecimal monto = new BigDecimal(pago.getMonto().toString());

        // Restar el monto del pago al monto restante de la deuda
        deuda.setMontoRestante(deuda.getMontoRestante().subtract(monto));

        // Si el monto restante llega a cero o menos, cambiar el estado de la deuda a "Pagada"
        if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
            deuda.setEstadoDeuda(EstadoDeuda.Pagado);
        }

        // Guarda la deuda actualizada
        deudaRepository.save(deuda);

        // Guarda el pago asociado a la deuda
        return pagoRepository.save(pago);
    }
}
