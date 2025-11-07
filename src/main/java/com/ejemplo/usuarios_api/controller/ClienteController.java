package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.ClienteDTO;
import com.ejemplo.usuarios_api.dto.ClienteSaldoPendienteDTO;
import com.ejemplo.usuarios_api.dto.DeudaDTO;
import com.ejemplo.usuarios_api.dto.PagoDTO;
import com.ejemplo.usuarios_api.model.Cliente;
import com.ejemplo.usuarios_api.service.ClienteService;
import com.ejemplo.usuarios_api.service.DeudaService;
import com.ejemplo.usuarios_api.service.PagoService;
import com.ejemplo.usuarios_api.util.CsvUtil;
import com.ejemplo.usuarios_api.util.ExcelUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Log4j2
public class ClienteController {

    @Autowired
    private ExcelUtil excelUtil;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DeudaService deudaService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private CsvUtil csvUtil;

    // Obtener todos los clientes
    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> obtenerClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ClienteDTO> clientes = clienteService.obtenerTodosLosClientes(page, size);
        return ResponseEntity.ok(clientes);
    }

    // Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.crearCliente(cliente);
        ClienteDTO clienteDTO = clienteService.convertirClienteAClienteDTO(nuevoCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDTO);
    }

    // Actualizar un cliente existente
    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long clienteId, @RequestBody ClienteDTO clienteActualizado) {
        Cliente cliente = clienteService.actualizarCliente(clienteId, clienteActualizado);
        ClienteDTO clienteDTO = clienteService.convertirClienteAClienteDTO(cliente);
        return ResponseEntity.ok(clienteDTO);
    }

    // Eliminar un cliente
    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long clienteId) {
        clienteService.eliminarCliente(clienteId);
        return ResponseEntity.noContent().build();
    }

    // Manejo de errores global
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Obtener deudas por cliente
    @GetMapping("/{clienteId}/deudas")
    public ResponseEntity<List<DeudaDTO>> obtenerDeudasPorCliente(@PathVariable Long clienteId) {
        List<DeudaDTO> deudas = deudaService.obtenerDeudasPorCliente(clienteId);
        return ResponseEntity.ok(deudas);
    }

    // Obtener pagos por cliente
    @GetMapping("/{clienteId}/pagos")
    public ResponseEntity<List<PagoDTO>> obtenerPagosPorCliente(@PathVariable Long clienteId) {
        List<PagoDTO> pagos = pagoService.obtenerPagosPorCliente(clienteId);
        return ResponseEntity.ok(pagos);
    }

    // Obtener datos completos de un cliente
    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long clienteId) {
        ClienteDTO clienteDTO = clienteService.obtenerClientePorId(clienteId);
        return ResponseEntity.ok(clienteDTO);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<ByteArrayResource> exportarClientesConSaldoPendienteExcel(
            @RequestParam(value = "mes") Integer mes,
            @RequestParam(value = "anio") Integer anio) {
        try {
            System.out.println("Iniciando exportación de Excel para mes: " + mes + ", año: " + anio);

            // Llamar al servicio
            List<ClienteSaldoPendienteDTO> clientes = clienteService.obtenerClientesConSaldoPendientePorFecha(mes, anio);

            if (clientes.isEmpty()) {
                System.out.println("No se encontraron clientes con saldo pendiente para el mes y año proporcionados.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // Generar el archivo Excel
            byte[] excelBytes = excelUtil.generarExcelClientesConSaldo(clientes);

            ByteArrayResource resource = new ByteArrayResource(excelBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_saldo_pendiente.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(excelBytes.length)
                    .body(resource);
        } catch (Exception e) {
            log.error("Error al exportar clientes con saldo pendiente: {}", e.getMessage());
            log.error(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    // Endpoint para descargar la lista de clientes con saldo pendiente en CSV (Opcional)
    @GetMapping("/exportar/csv")
    public ResponseEntity<ByteArrayResource> exportarClientesConSaldoPendienteCSV() {
        try {
            List<ClienteSaldoPendienteDTO> clientes = clienteService.obtenerClientesConSaldoPendiente();
            String csvContenido = csvUtil.generarCsvClientesConSaldo(clientes);
            byte[] csvBytes = csvContenido.getBytes(StandardCharsets.UTF_8);

            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clientes_saldo_pendiente.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvBytes.length)
                    .body(resource);
        } catch (Exception e) {
            // Manejar excepción según corresponda
            return ResponseEntity.status(500).build();
        }
    }
}
