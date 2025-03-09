package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.PagoDTO;
import com.ejemplo.usuarios_api.model.MetodoPago;
import com.ejemplo.usuarios_api.service.DeudaService;
import com.ejemplo.usuarios_api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private DeudaService deudaService;

    // Obtener todos los pagos
    @GetMapping
    public ResponseEntity<List<PagoDTO>> obtenerPagos() {
        List<PagoDTO> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }

    // Obtener pagos asociados a una deuda específica
    @GetMapping("/deuda/{deudaId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorDeuda(@PathVariable Long deudaId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorDeuda(deudaId);
        return ResponseEntity.ok(pagos);
    }

    @PostMapping("/{deudaId}/registrar")
    public ResponseEntity<PagoDTO> registrarPago(
            @PathVariable Long deudaId,
            @RequestParam("montoPago") double montoPago,
            @RequestParam(value = "comprobante", required = false) MultipartFile comprobante,
            @RequestParam("fechaPagoReal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPagoReal,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam(value = "observaciones", required = false) String observaciones
    ) {
        try {
            PagoDTO pagoDTO = new PagoDTO();
            pagoDTO.setMonto(BigDecimal.valueOf(montoPago));
            pagoDTO.setMetodoPago(metodoPago);
            pagoDTO.setFechaTransaccion(fechaPagoReal);
            pagoDTO.setObservaciones(observaciones);

            byte[] comprobanteBytes = null;
            String contentType = null;
            if (comprobante != null && !comprobante.isEmpty()) {
                comprobanteBytes = comprobante.getBytes();
                contentType = comprobante.getContentType();
            }

            PagoDTO pagoRegistrado = pagoService.registrarPago(
                    deudaId,
                    pagoDTO,
                    comprobanteBytes,
                    contentType
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(pagoRegistrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }




    // Obtener pagos realizados por un cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorCliente(@PathVariable Long clienteId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // Cancelar un pago y ajustar la deuda asociada
    @DeleteMapping("/cancelar/{pagoId}")
    public ResponseEntity<Void> cancelarPago(@PathVariable Long pagoId) {
        Long deudaId = pagoService.obtenerDeudaIdPorPagoId(pagoId);
        pagoService.cancelarPago(pagoId);
        deudaService.actualizarEstadoDeuda(deudaId); // Recalcular el estado de la deuda después de la cancelación
        return ResponseEntity.noContent().build();
    }

    // Obtener el total de pagos realizados por una deuda específica
    @GetMapping("/deuda/{deudaId}/total")
    public ResponseEntity<Double> obtenerTotalPagosPorDeuda(@PathVariable Long deudaId) {
        Double totalPagos = pagoService.obtenerTotalPagosPorDeuda(deudaId);
        return ResponseEntity.ok(totalPagos);
    }

    // Obtener pagos en un rango de fechas para una deuda específica
    @GetMapping("/deuda/{deudaId}/rango-fechas")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorRangoDeFechas(
            @PathVariable Long deudaId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorRangoDeFechas(deudaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(pagos);
    }
    @GetMapping("/comprobante/{pagoId}")
    public ResponseEntity<byte[]> obtenerComprobantePago(@PathVariable Long pagoId) {
        try {
            System.out.println("Buscando comprobante para pagoId: " + pagoId);

            Map<String, Object> comprobanteData = pagoService.obtenerComprobante(pagoId);
            if (comprobanteData == null) {
                System.out.println("No se encontró comprobante para el pagoId: " + pagoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Verificar las claves presentes
            System.out.println("Datos del comprobante: " + comprobanteData.keySet());

            byte[] comprobante = (byte[]) comprobanteData.get("comprobante");
            String formato = (String) comprobanteData.get("formato");

            if (comprobante == null || formato == null) {
                System.out.println("Comprobante o formato es nulo para el pagoId: " + pagoId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            MediaType mediaType;
            if ("image/png".equalsIgnoreCase(formato)) {
                mediaType = MediaType.IMAGE_PNG;
            } else if ("image/jpeg".equalsIgnoreCase(formato)) { // Añadido
                mediaType = MediaType.IMAGE_JPEG;
            } else if ("application/pdf".equalsIgnoreCase(formato)) {
                mediaType = MediaType.APPLICATION_PDF;
            } else {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDisposition(ContentDisposition.builder("inline")
                    .filename("comprobante_" + pagoId + "." + formato.split("/")[1])
                    .build());

            return new ResponseEntity<>(comprobante, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error al obtener el comprobante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
