package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.MovimientoDTO;
import com.ejemplo.usuarios_api.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RequestMapping("/api/clientes")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @GetMapping("/{clienteId}/movimientos")
    public ResponseEntity<List<MovimientoDTO>> obtenerMovimientos(@PathVariable Long clienteId) {
        List<MovimientoDTO> movimientos = movimientoService.obtenerMovimientos(clienteId);
        return ResponseEntity.ok(movimientos);
    }
}
