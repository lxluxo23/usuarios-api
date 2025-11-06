package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.dto.IndicadoresDTO;
import com.ejemplo.usuarios_api.service.IndicadoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class IndicadoresController {

    @Autowired
    private IndicadoresService indicadoresService;

    @GetMapping("/{clienteId}/indicadores")
    public ResponseEntity<IndicadoresDTO> obtenerIndicadores(@PathVariable Long clienteId) {
        IndicadoresDTO indicadores = indicadoresService.obtenerIndicadores(clienteId);
        return ResponseEntity.ok(indicadores);
    }
}
