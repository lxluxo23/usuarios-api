package com.ejemplo.usuarios_api.repository;

import com.ejemplo.usuarios_api.model.Deuda;
import com.ejemplo.usuarios_api.model.TipoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {
    List<Deuda> findByCliente_ClienteId(Long clienteID); // Relación Cliente -> Propiedad clienteID

    @Query("SELECT d FROM Deuda d WHERE d.cliente.clienteId = :clienteId AND d.tipoDeuda = :tipoDeuda AND YEAR(d.fechaInicio) = :anio")
    List<Deuda> findHonorariosByClienteAndAnio(Long clienteId, TipoDeuda tipoDeuda, int anio);
    List<Deuda> findAllByClienteClienteId(Long clienteId);
}
