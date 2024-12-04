package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.EstadoDeuda;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;  // Asegúrate de importar BigDecimal
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DeudaRepository deudaRepository;

    // Obtener todos los pagos
    @GetMapping
    public List<Pago> obtenerPagos() {
        return pagoRepository.findAll();
    }

    // Crear un pago y asociarlo a una deuda específica
    @PostMapping("/registrar/{deudaId}")
    public ResponseEntity<Pago> crearPago(@PathVariable Long deudaId, @RequestBody Pago pago) {
        // Buscar la deuda por ID
        Optional<Deuda> deudaOpt = deudaRepository.findById(deudaId);

        if (deudaOpt.isPresent()) {
            Deuda deuda = deudaOpt.get();
            pago.setDeuda(deuda);  // Asocia el pago a la deuda encontrada

            // Asigna la fecha de transacción (fecha actual)
            if (pago.getFechaTransaccion() == null) {
                pago.setFechaTransaccion(LocalDate.now());
            }

            // Convierte el monto recibido a BigDecimal si es necesario
            if (pago.getMonto() != null) {
                // Si el monto es pasado como un String o Double, se convierte a BigDecimal
                pago.setMonto(new BigDecimal(pago.getMonto().toString()));  // Asegura que el monto es un BigDecimal
            }

            // Restar el monto del pago al monto restante de la deuda
            BigDecimal nuevoMontoRestante = deuda.getMontoRestante().subtract(pago.getMonto());

            // Redondear el resultado a 2 decimales para evitar errores de precisión
            nuevoMontoRestante = nuevoMontoRestante.setScale(2, RoundingMode.HALF_UP);

            // Verificar el monto restante antes de la comparación
            System.out.println("Nuevo monto restante: " + nuevoMontoRestante);

            deuda.setMontoRestante(nuevoMontoRestante);

            // Si el monto restante llega a cero o menos, cambiar el estado de la deuda a "Pagada"
            if (deuda.getMontoRestante().compareTo(BigDecimal.ZERO) <= 0) {
                deuda.setEstadoDeuda(EstadoDeuda.Pagado);
            }

            // Guarda la deuda actualizada
            deudaRepository.save(deuda);

            // Guarda el pago asociado a la deuda
            Pago pagoGuardado = pagoRepository.save(pago);
            return ResponseEntity.ok(pagoGuardado); // Devuelve el pago guardado
        } else {
            // Si no se encuentra la deuda, retorna un error 404
            return ResponseEntity.notFound().build();
        }
    }
}
