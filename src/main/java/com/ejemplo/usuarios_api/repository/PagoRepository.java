package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByDeudaDeudaId(Long deudaID); // Relación Deuda -> Propiedad deudaID
}
