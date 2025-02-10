package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.HonorarioContableDTO;
import com.ejemplo.usuarios_api.dto.HonorarioRequest;
import com.ejemplo.usuarios_api.dto.MesHonorarioDTO;
import com.ejemplo.usuarios_api.model.MetodoPago;
import com.ejemplo.usuarios_api.service.HonorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/honorarios")
@CrossOrigin(origins = "", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class HonorarioController {

    @Autowired
    private HonorarioService honorarioService;

    // Crear un honorario contable
    @PostMapping("/{clienteId}")
    public ResponseEntity<?> crearHonorarioContable(
            @PathVariable Long clienteId,
            @RequestBody HonorarioRequest honorarioRequest) {
        try {
            honorarioService.crearHonorarioContable(clienteId, honorarioRequest.getMontoMensual());
            return ResponseEntity.ok(Map.of("message", "Honorario contable creado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el honorario contable: " + e.getMessage()));
        }
    }

    // Registrar un pago para un mes específico
    @PostMapping("/{honorarioId}/pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable Long honorarioId,
            @RequestParam("mes") int mes,
            @RequestParam("montoPago") double montoPago,
            @RequestParam("comprobante") MultipartFile comprobante,
            @RequestParam("fechaPagoReal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPagoReal,
            @RequestParam("metodoPago") MetodoPago metodoPago
            ) {
        try {
            honorarioService.registrarPago(honorarioId, mes, montoPago, comprobante.getBytes(), fechaPagoReal, metodoPago);

            return ResponseEntity.ok(Map.of("message", "Pago registrado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }

    // Obtener detalles de un honorario contable
    @GetMapping("/{honorarioId}")
    public ResponseEntity<?> obtenerDetallesHonorario(@PathVariable Long honorarioId) {
        try {
            HonorarioContableDTO honorarioDTO = honorarioService.obtenerDetallesHonorario(honorarioId);
            return ResponseEntity.ok(honorarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener los detalles: " + e.getMessage()));
        }
    }

    // Obtener honorarios por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<HonorarioContableDTO>> obtenerHonorariosPorCliente(@PathVariable Long clienteId) {
        List<HonorarioContableDTO> honorariosDTO = honorarioService.obtenerHonorariosPorCliente(clienteId);
        return ResponseEntity.ok(honorariosDTO);
    }

    @GetMapping("/{honorarioId}/detalle")
    public ResponseEntity<?> obtenerDetalleHonorario(@PathVariable Long honorarioId) {
        try {
            HonorarioContableDTO honorario = honorarioService.obtenerDetallesHonorario(honorarioId);
            return ResponseEntity.ok(honorario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo obtener el detalle del honorario: " + e.getMessage()));
        }
    }

    @GetMapping("/{honorarioId}/mes/{mes}")
    public ResponseEntity<?> obtenerDetalleMesHonorario(
            @PathVariable Long honorarioId,
            @PathVariable int mes) {
        try {
            MesHonorarioDTO mesHonorario = honorarioService.obtenerDetalleMes(honorarioId, mes);
            return ResponseEntity.ok(mesHonorario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo obtener el detalle del mes: " + e.getMessage()));
        }
    }


    @GetMapping("/pagos/comprobante/{pagoId}")
    public ResponseEntity<byte[]> obtenerComprobante(@PathVariable Long pagoId) {
        try {
            Map<String, Object> comprobanteData = honorarioService.obtenerComprobante(pagoId);
            byte[] comprobante = (byte[]) comprobanteData.get("comprobante");
            String formato = (String) comprobanteData.get("formato");

            // Configurar el tipo de contenido basado en el formato
            MediaType mediaType;
            if ("image/png".equalsIgnoreCase(formato)) {
                mediaType = MediaType.IMAGE_PNG;
            } else if ("application/pdf".equalsIgnoreCase(formato)) {
                mediaType = MediaType.APPLICATION_PDF;
            } else {
                mediaType = MediaType.APPLICATION_OCTET_STREAM; // Tipo genérico
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);

            return new ResponseEntity<>(comprobante, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @PutMapping("/{honorarioId}/mes/{mes}")
    public ResponseEntity<?> editarHonorarioPorMes(
            @PathVariable Long honorarioId,
            @PathVariable int mes,
            @RequestParam("nuevoMontoMensual") double nuevoMontoMensual) {
        try {
            honorarioService.editarHonorarioPorMes(honorarioId, mes, nuevoMontoMensual);
            return ResponseEntity.ok(Map.of("message", "Honorario actualizado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al editar el honorario: " + e.getMessage()));
        }
    }
    // Eliminar un honorario contable
    @DeleteMapping("/{honorarioId}")
    public ResponseEntity<?> eliminarHonorario(@PathVariable Long honorarioId) {
        try {
            honorarioService.eliminarHonorario(honorarioId);
            return ResponseEntity.ok(Map.of("message", "Honorario contable eliminado con éxito."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar el honorario: " + e.getMessage()));
        }
    }

}
