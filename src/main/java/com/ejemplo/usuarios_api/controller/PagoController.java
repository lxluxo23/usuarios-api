package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Para manejar valores monetarios con precisión
import java.math.RoundingMode; // Para redondeo de valores
import java.time.LocalDate; // Para trabajar con fechas
import java.util.Optional;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository; // Inyección del repositorio de pagos

    @Autowired
    private DeudaRepository deudaRepository; // Inyección del repositorio de deudas

    // Obtener todos los pagos
    @GetMapping
    public List<Pago> obtenerPagos() {
        // Devuelve todos los pagos registrados en la base de datos
        return pagoRepository.findAll();
    }

    // Crear un nuevo pago y asociarlo a una deuda específica
    @PostMapping("/registrar/{deudaId}")
    public ResponseEntity<Pago> crearPago(@PathVariable Long deudaId, @RequestBody Pago pago) {
        // Busca la deuda por su ID
        Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId);

        if (deudaOpt.isPresent()) {
            Deuda deuda = deudaOpt.get();
            pago.setDeuda(deuda); // Asocia el pago a la deuda encontrada

            // Asigna la fecha de transacción si no está definida
            if (pago.getFechaTransaccion() == null) {
                pago.setFechaTransaccion(LocalDate.now());
            }

            // Convierte el monto recibido a BigDecimal si no lo es
            if (pago.getMonto() != null) {
                pago.setMonto(new BigDecimal(pago.getMonto().toString())); // Asegura que el monto es un BigDecimal
            }

            // Resta el monto del pago al monto restante de la deuda
            BigDecimal nuevoMontoRestante = deuda.getMontoRestante().subtract(pago.getMonto());

            // Redondea el nuevo monto restante a 2 decimales
            nuevoMontoRestante = nuevoMontoRestante.setScale(2, RoundingMode.HALF_UP);

            deuda.setMontoRestante(nuevoMontoRestante); // Actualiza el monto restante de la deuda

            // Cambia el estado de la deuda a "Pagada" si el monto restante es cero o negativo
            if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
                deuda.setEstadoDeuda(EstadoDeuda.Pagado);
            }

            // Guarda la deuda actualizada en la base de datos
            deudaRepository.save(deuda);

            // Guarda el pago en la base de datos
            Pago pagoGuardado = pagoRepository.save(pago);

            // Retorna el pago guardado como respuesta
            return ResponseEntity.ok(pagoGuardado);
        } else {
            // Retorna un error 404 si no se encuentra la deuda
            return ResponseEntity.notFound().build();
        }
    }
}
