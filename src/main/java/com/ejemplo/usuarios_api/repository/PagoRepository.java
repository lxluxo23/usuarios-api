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
    List<Pago> findByDeudaDeudaId(Long deudaID);
    //List<Pago> findByDeuda_Cliente_ClienteId(Long clienteID);
    @Query("SELECT p FROM Pago p WHERE p.deuda.cliente.clienteId = :clienteId")
    List<Pago> findPagosByClienteId(@Param("clienteId") Long clienteId);
    List<Pago> findByDeudaAndMes(Deuda deuda, int mes);
}
