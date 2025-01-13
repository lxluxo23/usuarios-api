package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Consulta para encontrar pagos por cliente
    @Query("SELECT p FROM Pago p WHERE p.deuda.cliente.clienteId = :clienteId")
    List<Pago> findPagosByClienteId(@Param("clienteId") Long clienteId);

    // Consulta automática para encontrar pagos por deuda
    List<Pago> findByDeudaDeudaId(Long deudaId);

    // Consulta para encontrar pagos por deuda y mes
    @Query("SELECT p FROM Pago p WHERE p.deuda = :deuda AND p.mes = :mes")
    List<Pago> findByDeudaAndMes(@Param("deuda") Deuda deuda, @Param("mes") int mes);

    @Query("SELECT p FROM Pago p WHERE p.deuda.cliente.clienteId = :clienteId")
    List<Pago> findByDeudaClienteClienteId(@Param("clienteId") Long clienteId);

}
