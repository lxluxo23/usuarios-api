package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {
    List<Historial> findByDeudaDeudaId(Long deudaId);
    List<Historial> findByPagoPagoId(Long pagoId);
}
