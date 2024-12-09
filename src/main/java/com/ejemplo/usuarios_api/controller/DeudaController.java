package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.*;
import com.ejemplo.usuarios_api.repository.ClienteRepository;
import com.ejemplo.usuarios_api.repository.DeudaRepository;
import com.ejemplo.usuarios_api.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/deudas")
public class DeudaController {

    @Autowired
    private DeudaRepository deudaRepository; // Inyeccion del repositorio de deudas

    @Autowired
    private ClienteRepository clienteRepository; // Inyeccion del repositorio de clientes

    @Autowired
    private PagoRepository pagoRepository; // Inyección de PagoRepository


    // Obtener todas las deudas
    @GetMapping
    public List<Deuda> obtenerDeudas() {
        //devuelve todas las deudas registradas
        return deudaRepository.findAll();
    }

    // Crear una nueva deuda
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Deuda crearDeuda(@RequestBody Deuda deuda) {
        // Valida que la deuda tenga un cliente asociado
        if (deuda.getCliente() == null || deuda.getCliente().getClienteId() == null) {
            throw new IllegalArgumentException("El cliente asociado a la deuda es requerido.");
        }

        // Busca al cliente asociado a la deuda
        Cliente cliente = clienteRepository.findById(deuda.getCliente().getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + deuda.getCliente().getClienteId()));

        // Asigna el cliente encontrado a la deuda
        deuda.setCliente(cliente);

        // Guarda la deuda en la base de datos
        return deudaRepository.save(deuda);
    }

    @PostMapping("/{clienteId}/honorario-contable")
    public ResponseEntity<?> agregarHonorarioContable(@PathVariable Long clienteId, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal montoMensual = new BigDecimal(request.get("montoMensual").toString());
            List<Integer> mesesPagados = request.containsKey("mesesPagados") ? (List<Integer>) request.get("mesesPagados") : new ArrayList<>();

            int anioActual = LocalDate.now().getYear();

            // Verificar si ya existe un honorario contable para el cliente en el año actual
            List<Deuda> honorariosExistentes = deudaRepository.findHonorariosByClienteAndAnio(clienteId, TipoDeuda.Honorario_contable, anioActual);
            if (!honorariosExistentes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ya existe un honorario contable para el año actual.");
            }

            // Crear nueva deuda
            Deuda nuevaDeuda = new Deuda();
            nuevaDeuda.setCliente(clienteRepository.findById(clienteId).orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
            nuevaDeuda.setTipoDeuda(TipoDeuda.Honorario_contable);
            nuevaDeuda.setMontoTotal(montoMensual.multiply(BigDecimal.valueOf(12))); // El total es 12 veces el monto mensual
            nuevaDeuda.setMontoRestante(montoMensual.multiply(BigDecimal.valueOf(12)));
            nuevaDeuda.setFechaCreacion(LocalDate.now());
            nuevaDeuda.setFechaInicio(LocalDate.now());
            nuevaDeuda.setFechaVencimiento(LocalDate.now().plusYears(1).minusDays(1));
            nuevaDeuda.setObservaciones("Honorario Contable");

            Deuda deudaGuardada = deudaRepository.save(nuevaDeuda);

            // Crear cuotas mensuales
            BigDecimal montoRestante = montoMensual.multiply(BigDecimal.valueOf(12));
            boolean todosPagados = true; // Bandera para verificar si todos los pagos se han realizado

            for (int i = 0; i < 12; i++) {
                Pago cuota = new Pago();
                cuota.setDeuda(deudaGuardada);
                cuota.setMonto(montoMensual);
                cuota.setFechaTransaccion(LocalDate.of(anioActual, i + 1, 1)); // Define la fecha de la cuota al inicio de cada mes
                cuota.setObservaciones("Cuota " + (i + 1) + " de Honorario Contable");

                // Si el mes está en la lista de meses pagados, se marca como Pagado, si no, como Pendiente
                if (mesesPagados.contains(i + 1)) {
                    cuota.setEstado(EstadoDeuda.Pagado);  // Asigna el estado Pagado
                    montoRestante = montoRestante.subtract(montoMensual); // Restar el monto de la cuota pagada del monto restante
                } else {
                    cuota.setEstado(EstadoDeuda.Pendiente); // Asigna el estado Pendiente
                    todosPagados = false; // Si hay cuotas pendientes, el estado de la deuda será pendiente
                }

                // Guardamos la cuota en la base de datos
                pagoRepository.save(cuota);
            }

            // Si todas las cuotas están pagadas, actualizamos el estado de la deuda a "Pagada"
            if (todosPagados) {
                deudaGuardada.setEstadoDeuda(EstadoDeuda.Pagado);
            } else {
                deudaGuardada.setEstadoDeuda(EstadoDeuda.Pendiente);
            }

            // Actualizamos el monto restante de la deuda (se resta el monto de las cuotas pagadas)
            deudaGuardada.setMontoRestante(montoRestante);

            // Guardamos la deuda actualizada
            deudaRepository.save(deudaGuardada);

            return ResponseEntity.ok("Honorario Contable registrado con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el honorario contable: " + e.getMessage());
        }
    }
}