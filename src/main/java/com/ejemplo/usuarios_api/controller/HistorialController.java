package com.ejemplo.usuarios_api.controller;

import com.ejemplo.usuarios_api.model.Historial;
import com.ejemplo.usuarios_api.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialRepository historialRepository;

    @GetMapping
    public List<Historial> obtenerHistorial() {
        return historialRepository.findAll();
    }

    @PostMapping
    public Historial crearHistorial(@RequestBody Historial historial) {
        return historialRepository.save(historial);
    }
}
