package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Historial;
import com.ejemplo.usuarios_api.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialRepository historialRepository;

    // Obtener todos los registros del historial
    @GetMapping
    public List<Historial> obtenerHistorial() {
        return historialRepository.findAll();
    }

    // Crear un nuevo registro en el historial
    @PostMapping
    public Historial crearHistorial(@RequestBody Historial historial) {
        // Guarda un nuevo registro del historial en la base de datos
        return historialRepository.save(historial);
    }
}
