package com.ejemplo.usuarios_api.service;

import com.ejemplo.usuarios_api.model.Historial;
import com.ejemplo.usuarios_api.model.Pago;
import com.ejemplo.usuarios_api.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    public void registrarCambioDeuda(Pago pago, String descripcion) {
        Historial historial = new Historial();
        historial.setDeuda(pago.getDeuda());
        historial.setPago(pago);
        historial.setDescripcion(descripcion);
        historialRepository.save(historial);
    }
}
